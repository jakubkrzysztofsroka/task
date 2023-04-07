package com.jsroka.task.services

import cats.effect.IO
import cats.effect.kernel.Async
import cats.implicits._
import cats.instances._
import com.jsroka.task.configuration.KafkaConfiguration
import com.jsroka.task.services.file.CsvReadingService
import fs2._
import fs2.kafka._

class KafkaSumProducer[F[_]: Async](
  csvReadingService: CsvReadingService[F, Int],
  kafkaConfiguration: KafkaConfiguration
) {

  private val producerSettings =
    ProducerSettings[F, String, Int]
      .withBootstrapServers(kafkaConfiguration.address)

  def produceSumsFromFile(fileName: String, numberOfStreams: Int): F[Unit] = {
    csvReadingService
      .readCsv(fileName)
      .broadcastThrough(getProducers(numberOfStreams): _*)
      .compile
      .drain
  }

  private def getProducers(numberOfStreams: Int): List[Pipe[F, Int, ProducerResult[Unit, String, Int]]] =
    (0 until numberOfStreams).map(remainder => singleSumAndProducePipe(remainder, numberOfStreams)).toList

  private def singleSumAndProducePipe(
    remainder: Int,
    numberOfStreams: Int
  ): Pipe[F, Int, ProducerResult[Unit, String, Int]] =
    _.filter(v => v % numberOfStreams == remainder).chunkAll
      .map(_.toList.sum)
      .map(toProducerRecord(remainder, _))
      .map(a => ProducerRecords.one[String, Int](a))
      .through(KafkaProducer.pipe(producerSettings))

  private def toProducerRecord(modulo: Int, sum: Int): ProducerRecord[String, Int] =
    ProducerRecord(getTopicName(modulo, kafkaConfiguration.topicPrefix), modulo.toString, sum)

  private def getTopicName(moduloValue: Int, prefix: String) = s"$prefix$moduloValue"
}
