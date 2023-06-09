package com.jsroka.task.services.queue.producer

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.jsroka.task.configuration.AppConfiguration
import com.jsroka.task.services.file.CsvIntReader
import com.jsroka.task.services.queue.admin.KafkaAdminService
import com.jsroka.task.utils.KafkaUtils
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class KafkaSumProducerSpec
  extends AnyFlatSpecLike
  with Matchers
  with AppConfiguration
  with KafkaUtils
  with BeforeAndAfterAll {

  private val topicSuffixes = (0 until configuration.modulo).map(_.toString).toList
  override def beforeAll(): Unit =
    queueAdminService.createTopics(topicSuffixes).unsafeRunSync()

  override def afterAll(): Unit = queueAdminService.deleteTopics(topicSuffixes).unsafeRunSync()

  private val queueAdminService = new KafkaAdminService[IO](configuration.kafka)

  it should "read file create N topics according to configuration and produce sum for all" +
    " values that have the same remainder value for modulo N operation." in {

      val kafkaSumProducer = new KafkaSumProducer[IO](new CsvIntReader[IO], configuration.kafka, configuration.modulo)

      kafkaSumProducer
        .produce(fileName = configuration.fileName)
        .unsafeRunSync()

      getOneValueFromTopic(configuration.kafka.topicPrefix + "0") shouldEqual "18"
      getOneValueFromTopic(configuration.kafka.topicPrefix + "1") shouldEqual "22"
      getOneValueFromTopic(configuration.kafka.topicPrefix + "2") shouldEqual "15"

    }
}
