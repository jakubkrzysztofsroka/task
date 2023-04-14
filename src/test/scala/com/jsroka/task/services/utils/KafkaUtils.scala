package com.jsroka.task.services.utils

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.jsroka.task.configuration.AppConfiguration
import fs2.kafka.AutoOffsetReset
import fs2.kafka.ConsumerSettings
import fs2.kafka.KafkaConsumer

trait KafkaUtils { _: AppConfiguration =>

  protected val consumerSettings =
    ConsumerSettings[IO, String, String]
      .withBootstrapServers(configuration.kafka.address)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withGroupId("group")

  protected def getOneValueFromTopic(topic: String): String = {
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .take(1)
      .compile
      .toList
      .map(_.head.record.value)
      .unsafeRunSync()
  }
}
