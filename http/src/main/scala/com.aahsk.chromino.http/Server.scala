package com.aahsk.chromino.http

import cats.implicits._
import cats.{Defer, Monad}
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Timer}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import com.aahsk.chromino.http.game.GameRoute
import scala.concurrent.ExecutionContext

object Server {
  def routes[F[_]: Monad: Defer: Concurrent](): HttpApp[F] = {
    val gameRoutes: HttpRoutes[F] = new GameRoute[F]().createRoutes();
    val healthRoutes: HttpRoutes[F] = new HealthRoute[F]().createRoutes()

    gameRoutes <+> healthRoutes
  }.orNotFound

  def run[F[_]: Timer: ContextShift: ConcurrentEffect]()(implicit
      EC: ExecutionContext
  ): F[Unit] = {
    BlazeServerBuilder[F](EC)
      .bindHttp(port = 9000, host = "localhost")
      .withHttpApp(routes())
      .serve
      .compile
      .drain
  }
}
