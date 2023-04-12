package com.jsroka.task.services.queue

import cats.effect.kernel.Async
import com.jsroka.task.configuration.KafkaConfiguration
import com.jsroka.task.services.file.CsvReadingService
import fs2._
import fs2.kafka._

class KafkaSumProducer[F[_]: Async](
  csvReadingService: CsvReadingService[F, Int],
  kafkaConfiguration: KafkaConfiguration,
  numberOfStreams: Int
) extends FromFileQueueProducer[F] {

  private val producerSettings =
    ProducerSettings[F, String, String]
      .withBootstrapServers(kafkaConfiguration.address)

  def produce(fileName: String): F[Unit] = {
    csvReadingService
      .readCsv(fileName)
      .broadcastThrough(getProducers(numberOfStreams): _*)
      .compile
      .drain
  }

  private def getProducers(numberOfStreams: Int): List[Pipe[F, Int, ProducerResult[Unit, String, String]]] =
    (0 until numberOfStreams).map(remainder => singleSumAndProducePipe(remainder, numberOfStreams)).toList

  private def singleSumAndProducePipe(
    remainder: Int,
    numberOfStreams: Int
  ): Pipe[F, Int, ProducerResult[Unit, String, String]] =
    _.filter(_ % numberOfStreams == remainder).chunkAll
      .map(_.toList.sum)
      .map(toProducerRecord(remainder, _))
      .map(ProducerRecords.one[String, String])
      .through(KafkaProducer.pipe(producerSettings))

  private def toProducerRecord(modulo: Int, sum: Int): ProducerRecord[String, String] = {
    println(s"producer record ${modulo}")
    ProducerRecord(getTopicName(modulo, kafkaConfiguration.topicPrefix), modulo.toString, sum.toString)
  }

  private def getTopicName(moduloValue: Int, prefix: String) = s"$prefix$moduloValue"
}
