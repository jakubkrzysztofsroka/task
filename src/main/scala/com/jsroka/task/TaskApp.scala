package com.jsroka.task

import cats.effect.IO
import cats.effect.IOApp
import com.jsroka.task.configuration.AppConfiguration
import com.jsroka.task.configuration.Configuration
import com.jsroka.task.services.file.CsvIntReader
import com.jsroka.task.services.file.CsvReadingService
import com.jsroka.task.services.http.HttpServer
import com.jsroka.task.services.http.Routes
import com.jsroka.task.services.http.TapirHttp4sServer
import com.jsroka.task.services.http.WaitOnKeyToTerminateServerLogic
import com.jsroka.task.services.queue.admin.{KafkaAdminService, QueueAdminService}
import com.jsroka.task.services.queue.consumer.KafkaStreamConsumer
import com.jsroka.task.services.queue.producer.{FromFileQueueProducer, KafkaSumProducer}
import org.http4s.server.Server
import scala.language.postfixOps

class TaskApp(configuration: Configuration) {

  private val queueAdminService: QueueAdminService[IO] = new KafkaAdminService[IO](configuration.kafka)
  private val csvReadingService: CsvReadingService[IO, Int] = new CsvIntReader[IO]
  private val fromFileQueueProducer: FromFileQueueProducer[IO] =
    new KafkaSumProducer(csvReadingService, configuration.kafka, configuration.modulo)
  private val queueStreamConsumer =
    new KafkaStreamConsumer[IO](configuration.kafka, configuration.readTopicFromBeginningOnConnection)
  private val routes = new Routes[IO](queueStreamConsumer)
  private val httpServer: HttpServer[IO, Server] = new TapirHttp4sServer[IO](configuration.http, routes)

  private def run(): IO[Unit] = {
    val topicSuffixes = (0 until configuration.modulo).map(_.toString).toList
    val program = for {
      _ <- queueAdminService.createTopics(topicSuffixes)
      _ <- fromFileQueueProducer.produce(configuration.fileName)
      _ <- httpServer.serve(WaitOnKeyToTerminateServerLogic.onServerRun)
      _ <- queueAdminService.deleteTopics(topicSuffixes)
    } yield ()

    program.handleErrorWith { _ =>
      println("Error occurred during application run. Deleting existing topics")
      queueAdminService.deleteTopics(topicSuffixes)
    }
  }
}

object TaskApp extends IOApp.Simple with AppConfiguration {
  override def run: IO[Unit] = new TaskApp(configuration).run()
}
