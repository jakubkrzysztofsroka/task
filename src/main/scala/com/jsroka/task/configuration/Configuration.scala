package com.jsroka.task.configuration

import com.typesafe.config.Config
import pureconfig.ConfigObjectSource
import pureconfig.ConfigSource
import pureconfig.generic.auto._

case class Configuration(
  fileName: String,
  modulo: Int,
  kafka: KafkaConfiguration
)

case class KafkaConfiguration(
  address: String,
  topicPrefix: String,
  numberOfPartitions: Int,
  replicationFactor: Int
)

object Configuration {
  def apply(config: Config): Configuration = {
    val configSource: ConfigObjectSource = ConfigSource.fromConfig(config.getConfig("task"))
    configSource.loadOrThrow[Configuration]
  }
}
