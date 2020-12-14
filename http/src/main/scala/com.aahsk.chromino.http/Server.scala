package com.aahsk.chromino.http

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import org.http4s.server.blaze.BlazeServerBuilder
import cats.implicits._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext

object Server {
  def run[F[_]: Timer: ContextShift: ConcurrentEffect](
    executionContext: ExecutionContext
  ): F[Unit] = {
    for {
      gameRoute <- GameRoute.create()
      _ <- BlazeServerBuilder[F](executionContext)
        .withNio2(true)
        .withWebSockets(true)
        .bindHttp(port = 9000, host = "localhost")
        .withHttpApp(gameRoute.orNotFound)
        .serve
        .compile
        .drain
    } yield ()
  }
}
