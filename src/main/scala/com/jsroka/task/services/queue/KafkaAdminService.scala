package com.jsroka.task.services.queue

import cats.effect._
import com.jsroka.task.configuration.KafkaConfiguration
import fs2.kafka._
import org.apache.kafka.clients.admin.NewTopic

class KafkaAdminService[F[_]: Async](configuration: KafkaConfiguration) extends QueueAdminService[F] {

  private val kafkaAdminClientResource: Resource[F, KafkaAdminClient[F]] =
    KafkaAdminClient.resource[F](AdminClientSettings(configuration.address))

  def createTopics(topicSuffixes: Seq[String]): F[Unit] =
    kafkaAdminClientResource.use(_.createTopics(topicSuffixes.map(createTopic)))

  override def deleteTopics(topicSuffixes: Seq[String]): F[Unit] =
    kafkaAdminClientResource.use(_.deleteTopics(topicSuffixes.map(s => configuration.topicPrefix + s)))

  private def createTopic(suffix: String): NewTopic =
    new NewTopic(
      configuration.topicPrefix + suffix,
      configuration.numberOfPartitions,
      configuration.replicationFactor.toShort
    )
}
