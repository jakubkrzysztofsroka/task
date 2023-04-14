package com.jsroka.task.services.http

import cats.effect.kernel.Async
import cats.effect.kernel.Sync
import com.jsroka.task.services.queue.QueueStreamConsumer
import fs2.Pipe
import sttp.capabilities.fs2.Fs2Streams
import sttp.tapir._

class Routes[F[_]: Async](queueStreamConsumer: QueueStreamConsumer[F]) {

  private val wsEndpoint = endpoint.get
    .in("ws")
    .in(query[Int]("number"))
    .out(webSocketBody[String, CodecFormat.TextPlain, String, CodecFormat.TextPlain](Fs2Streams[F]))

  private def oneSidedWebsocketForwardingQueueAndIgnoringInput(number: Int): Pipe[F, String, String] = { _ =>
    queueStreamConsumer.consume("sum-modulo-" + number)
  }

  val end =
    wsEndpoint.serverLogicSuccess(number => Sync[F].delay(oneSidedWebsocketForwardingQueueAndIgnoringInput(number)))

}
