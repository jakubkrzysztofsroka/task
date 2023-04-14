package com.jsroka.task.services.http

import cats.effect.IO
import org.http4s.server.Server

object WaitOnKeyToTerminateServerLogic {

  val onServerRun = (server: Server) => {
    for {
      _ <- IO.println(s"Server started at http://localhost:${server.address.getPort}. Press ENTER key to exit.")
      _ <- IO.readLine
    } yield ()
  }

}
