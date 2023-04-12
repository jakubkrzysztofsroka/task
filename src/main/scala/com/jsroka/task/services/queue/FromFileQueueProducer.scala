package com.jsroka.task.services.queue

trait FromFileQueueProducer[F[_]] {

  def produce(fileName: String): F[Unit]
}
