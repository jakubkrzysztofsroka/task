package com.jsroka.task.utils

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.jsroka.task.configuration.AppConfiguration
import fs2.kafka.AutoOffsetReset
import fs2.kafka.ConsumerSettings
import fs2.kafka.KafkaConsumer
import fs2.kafka.KafkaProducer
import fs2.kafka.ProducerRecord
import fs2.kafka.ProducerRecords
import fs2.kafka.ProducerSettings

trait KafkaUtils { _: AppConfiguration =>

  private val consumerSettings =
    ConsumerSettings[IO, String, String]
      .withBootstrapServers(configuration.kafka.address)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withGroupId("group")

  private val producerSettings =
    ProducerSettings[IO, String, String]
      .withBootstrapServers(configuration.kafka.address)

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

  protected def sendToKafka(topic: String, value: ProducerRecord[String, String]) = {
    fs2.Stream
      .emit[IO, ProducerRecord[String, String]](value)
      .map(ProducerRecords.one[String, String])
      .through(KafkaProducer.pipe(producerSettings))
      .compile
      .drain
      .unsafeRunSync()
  }
}
