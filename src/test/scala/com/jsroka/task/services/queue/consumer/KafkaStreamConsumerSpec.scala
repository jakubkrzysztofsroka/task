package com.jsroka.task.services.queue.consumer

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.jsroka.task.configuration.AppConfiguration
import com.jsroka.task.services.queue.admin.KafkaAdminService
import com.jsroka.task.utils.KafkaUtils
import fs2.kafka.ProducerRecord
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class KafkaStreamConsumerSpec
  extends AnyFlatSpecLike
  with Matchers
  with AppConfiguration
  with KafkaUtils
  with BeforeAndAfterAll {

  val kafkaConfiguration = configuration.kafka.copy(topicPrefix = "consumer-test-prefix-")

  private val queueAdminService = new KafkaAdminService[IO](kafkaConfiguration)

  private val topicSuffixes = Seq("0")
  override def beforeAll(): Unit =
    queueAdminService.createTopics(topicSuffixes).unsafeRunSync()

  override def afterAll(): Unit = queueAdminService.deleteTopics(topicSuffixes).unsafeRunSync()

  it should "consume values as a stream from single topic" in {
    val kafkaTopic = kafkaConfiguration.topicPrefix + "0"
    val values = List("1", "2", "3")
    values.foreach { value =>
      sendToKafka(kafkaTopic, ProducerRecord.apply(kafkaTopic, value, value))
    }

    val consumer = new KafkaStreamConsumer[IO](kafkaConfiguration, generateRandomConsumerGroup = true)
    consumer.consume(topicSuffix = "0").take(3).compile.toList.unsafeRunSync() shouldEqual values
  }

  it should "consume values as a stream from single topic for multiple clients," +
    " when generating random consumer group is enabled" in {
      val kafkaTopic = kafkaConfiguration.topicPrefix + "0"
      val values = List("1", "2", "3")
      values.foreach { value =>
        sendToKafka(kafkaTopic, ProducerRecord.apply(kafkaTopic, value, value))
      }

      val consumer = new KafkaStreamConsumer[IO](kafkaConfiguration, generateRandomConsumerGroup = true)
      val consumer2 = new KafkaStreamConsumer[IO](kafkaConfiguration, generateRandomConsumerGroup = true)
      consumer.consume(topicSuffix = "0").take(3).compile.toList.unsafeRunSync() shouldEqual values
      consumer2.consume(topicSuffix = "0").take(3).compile.toList.unsafeRunSync() shouldEqual values
    }

}
