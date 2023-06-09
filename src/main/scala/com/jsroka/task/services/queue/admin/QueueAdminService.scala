package com.jsroka.task.services.queue.admin

trait QueueAdminService[F[_]] {

  def createTopics(topicSuffixes: Seq[String]): F[Unit]

  def deleteTopics(topicSuffixes: Seq[String]): F[Unit]
}
