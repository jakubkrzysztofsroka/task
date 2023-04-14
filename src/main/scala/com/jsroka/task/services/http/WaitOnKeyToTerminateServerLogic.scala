package com.jsroka.task.services.http

import cats.effect.IO
import org.http4s.server.Server

object WaitOnKeyToTerminateServerLogic {

  val onServerRun = (server: Server) => {
    for {
      _ <- IO.println(s"Docs available at http://localhost:${server.address.getPort}/docs. Press ENTER key to exit.")
      _ <- IO.readLine
      _ <- IO.println("Closing the web server. Goodbye!")
    } yield ()
  }

}
