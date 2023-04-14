package com.jsroka.task.services.queue

import cats.effect.kernel.Async
import com.jsroka.task.configuration.KafkaConfiguration
import fs2.kafka.AutoOffsetReset
import fs2.kafka.ConsumerSettings
import fs2.kafka.KafkaConsumer
import java.util.UUID
class KafkaStreamConsumer[F[_]: Async](kafkaConfiguration: KafkaConfiguration, generateRandomConsumerGroup: Boolean)
  extends QueueStreamConsumer[F] {

  private def groupId: String = if (generateRandomConsumerGroup) UUID.randomUUID().toString else "TaskApp"

  private val consumerSettings =
    ConsumerSettings[F, String, String]
      .withBootstrapServers(kafkaConfiguration.address)
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withEnableAutoCommit(enableAutoCommit = true)

  override def consume(topicName: String): fs2.Stream[F, String] = KafkaConsumer
    .stream(consumerSettings.withGroupId(groupId))
    .subscribeTo(kafkaConfiguration.topicPrefix + topicName)
    .records
    .map(commitable => commitable.record.value)
}
