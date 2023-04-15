package com.jsroka.task.services.queue.admin

import cats.effect.IO
import cats.effect.Resource
import cats.effect.unsafe.implicits.global
import com.jsroka.task.configuration.KafkaConfiguration
import fs2.kafka.AdminClientSettings
import fs2.kafka.KafkaAdminClient
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class KafkaAdminServiceSpec extends AnyFlatSpecLike with Matchers {

  private val kafkaConfiguration =
    KafkaConfiguration(
      address = "localhost:9092",
      topicPrefix = "admin-test-prefix-",
      numberOfPartitions = 1,
      replicationFactor = 1
    )

  private val kafkaAdminClientResource: Resource[IO, KafkaAdminClient[IO]] =
    KafkaAdminClient.resource[IO](AdminClientSettings(kafkaConfiguration.address))

  it should "create and delete topics from kafka" in {

    val kafkaAdminService = new KafkaAdminService[IO](kafkaConfiguration)

    val topicNames = kafkaAdminClientResource.use(_.listTopics.names).unsafeRunSync()

    topicNames.count(_.contains("admin-test-prefix-")) shouldBe 0

    kafkaAdminService.createTopics(Seq("0", "1")).unsafeRunSync()

    val topicNamesAfterCreatingNewTopics = kafkaAdminClientResource.use(_.listTopics.names).unsafeRunSync()
    topicNamesAfterCreatingNewTopics.count(_.contains("admin-test-prefix-")) shouldBe 2

    kafkaAdminService.deleteTopics(Seq("0", "1")).unsafeRunSync()

    val topicNamesAfterRemovingCreatedTopics = kafkaAdminClientResource.use(_.listTopics.names).unsafeRunSync()
    topicNamesAfterRemovingCreatedTopics.count(_.contains("admin-test-prefix-")) shouldBe 0
  }
}
