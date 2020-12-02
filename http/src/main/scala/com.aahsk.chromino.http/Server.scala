package com.aahsk.chromino.http

import cats.implicits._
import cats.{Defer, Monad}
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, IO, Timer}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Server {
  def routes[F[_]: Monad: Defer: Concurrent](): HttpApp[F] = {
    val gameRoutes: HttpRoutes[F] = GameRoute.routes[F]();
    val healthRoutes: HttpRoutes[F] = HealthRoute.routes[F]()

    gameRoutes <+> healthRoutes
  }.orNotFound

  def run[F[_]: Timer: ContextShift: ConcurrentEffect](): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(port = 9000, host = "localhost")
      .withHttpApp(routes())
      .serve
      .compile
      .drain
}
