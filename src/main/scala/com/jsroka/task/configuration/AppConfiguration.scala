package com.jsroka.task.configuration

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

trait AppConfiguration {

  ConfigFactory.invalidateCaches()

  val config: Config = ConfigFactory
    .systemEnvironment()
    .withFallback(ConfigFactory.systemProperties())
    .withFallback(ConfigFactory.defaultApplication())
    .resolve()

  val configuration: Configuration = Configuration(config)
}
