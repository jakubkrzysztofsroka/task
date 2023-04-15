package com.jsroka.task.services.queue.consumer

trait QueueStreamConsumer[F[_]] {

  def consume(topicName: String): fs2.Stream[F, String]
}
