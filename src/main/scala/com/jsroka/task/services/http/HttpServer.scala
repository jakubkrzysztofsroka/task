package com.jsroka.task.services.http

import cats.effect.ExitCode

trait HttpServer[F[_], Server] {

  def serve(onServerRun: Server => F[Unit]): F[ExitCode]
}
