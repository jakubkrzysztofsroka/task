package com.jsroka.task.services.queue

import cats.effect.kernel.Async
import com.jsroka.task.configuration.KafkaConfiguration
import fs2.kafka.AutoOffsetReset
import fs2.kafka.ConsumerSettings
import fs2.kafka.KafkaConsumer
class KafkaStreamConsumer[F[_]: Async](kafkaConfiguration: KafkaConfiguration) extends QueueStreamConsumer[F] {

  private val consumerSettings =
    ConsumerSettings[F, String, String]
      .withBootstrapServers(kafkaConfiguration.address)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(enableAutoCommit = true)
      .withGroupId("group")
  override def consume(topicName: String): fs2.Stream[F, String] = KafkaConsumer
    .stream(consumerSettings)
    .subscribeTo(topicName)
    .records
    .map(commitable => commitable.record.value)
}
