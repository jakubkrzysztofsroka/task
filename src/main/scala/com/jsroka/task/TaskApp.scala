package com.jsroka.task

import cats.effect.IO
import cats.effect.IOApp
import com.jsroka.task.configuration.AppConfiguration
import com.jsroka.task.configuration.Configuration
import com.jsroka.task.services.file.CsvIntReader
import com.jsroka.task.services.file.CsvReadingService
import com.jsroka.task.services.queue.FromFileQueueProducer
import com.jsroka.task.services.queue.KafkaAdminService
import com.jsroka.task.services.queue.KafkaSumProducer
import com.jsroka.task.services.queue.QueueAdminService
import scala.language.postfixOps

class TaskApp(configuration: Configuration) {

  private val queueAdminService: QueueAdminService[IO] = new KafkaAdminService[IO](configuration.kafka)
  private val csvReadingService: CsvReadingService[IO, Int] = new CsvIntReader[IO]
  private val fromFileQueueProducer: FromFileQueueProducer[IO] =
    new KafkaSumProducer(csvReadingService, configuration.kafka, configuration.modulo)

  private def run(): IO[Unit] = {
    val topicSuffixes = (0 until configuration.modulo).map(_.toString).toList
    val program = for {
      _ <- queueAdminService.createTopics(topicSuffixes)
      _ <- fromFileQueueProducer.produce(configuration.fileName)
      _ <- queueAdminService.deleteTopics(topicSuffixes)
    } yield ()

    program.handleErrorWith { _ =>
      queueAdminService.deleteTopics(topicSuffixes)
    }
  }
}

object TaskApp extends IOApp.Simple with AppConfiguration {
  override def run: IO[Unit] = new TaskApp(configuration).run()
}
