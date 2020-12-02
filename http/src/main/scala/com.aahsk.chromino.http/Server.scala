package com.aahsk.chromino.http

import cats.implicits._
import cats.{Defer, Monad}
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, IO, Timer}
import fs2.Stream
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object Server {
  def routes[F[_]: Monad: Defer: Concurrent](): HttpApp[F] = {
    val chrominoRoutes: HttpRoutes[F] = ChrominoRoute.routes[F]();
    val echoRoute: HttpRoutes[F] = EchoRoute.routes[F]()

    chrominoRoutes <+> echoRoute
  }.orNotFound

  def run[F[_]]()(implicit
      EC: ExecutionContext,
      CS: ContextShift[F],
      C: ConcurrentEffect[F],
      T: Timer[F],
      SC: Stream.Compiler[F, F]
  ): F[Unit] =
    BlazeServerBuilder[F](EC)
      .bindHttp(port = 9000, host = "localhost")
      .withHttpApp(routes())
      .serve
      .compile
      .drain
}
