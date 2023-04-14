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

  /*
    Merging the input stream with queue stream isn't the best option as it does not make much sense to forward messages to user,
    but this enables us to shut down easily both streams and release web socket.
   */
  private def oneSidedWebsocketForwardingQueueAndIgnoringInput(number: Int): Pipe[F, String, String] = { inputStream =>
    inputStream.merge(queueStreamConsumer.consume(number.toString))
  }

  val websocketEndpoint =
    wsEndpoint.serverLogicSuccess(number => Sync[F].delay(oneSidedWebsocketForwardingQueueAndIgnoringInput(number)))

}
