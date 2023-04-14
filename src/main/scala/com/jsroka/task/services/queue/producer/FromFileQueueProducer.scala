package com.jsroka.task.services.queue.producer

trait FromFileQueueProducer[F[_]] {

  def produce(fileName: String): F[Unit]
}
