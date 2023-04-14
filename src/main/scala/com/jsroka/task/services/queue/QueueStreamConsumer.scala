package com.jsroka.task.services.queue

trait QueueStreamConsumer[F[_]] {

  def consume(topicName: String): fs2.Stream[F, String]
}
