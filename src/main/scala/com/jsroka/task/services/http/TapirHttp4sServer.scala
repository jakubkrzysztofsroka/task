package com.jsroka.task.services.http

import cats.effect.ExitCode
import cats.effect.kernel.Async
import cats.implicits.toFunctorOps
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import com.jsroka.task.configuration.HttpConfiguration
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.server.websocket.WebSocketBuilder2
import sttp.tapir.server.http4s.Http4sServerInterpreter

class TapirHttp4sServer[F[_]: Async](configuration: HttpConfiguration, routes: Routes[F])
  extends HttpServer[F, Server] {

  private val wsRoutes: WebSocketBuilder2[F] => HttpRoutes[F] =
    Http4sServerInterpreter[F]().toWebSocketRoutes(routes.end)

  def serve(onServerRun: Server => F[Unit]): F[ExitCode] = EmberServerBuilder
    .default[F]
    .withHost(Host.fromString(configuration.host).get)
    .withPort(Port.fromString(configuration.port).get)
    .withHttpWebSocketApp { wsb =>
      Router("/" -> wsRoutes(wsb)).orNotFound
    }
    .build
    .use { server =>
      onServerRun(server)
    }
    .as(ExitCode.Success)

}
