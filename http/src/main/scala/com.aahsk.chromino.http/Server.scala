package com.aahsk.chromino.http

import cats.implicits._
import cats.{Defer, Monad}
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Timer}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Server {
  def routes[F[_]: Monad: Defer: Concurrent](): HttpApp[F] = {
    val gameRoutes: HttpRoutes[F] = GameRoute.routes[F]();
    val healthRoutes: HttpRoutes[F] = HealthRoute.routes[F]()

    gameRoutes <+> healthRoutes
  }.orNotFound

  /**
    * Author's note: Everyone on the internet creates the BlazeServer
    *  as follows, but it seems that if you go-to-definition then the
    *  BlaseServer.apply uses ExecutionContext.global instead of
    *  the expected ContextShift. Not sure how to go about that :/
    */
  def run[F[_]: Timer: ContextShift: ConcurrentEffect](): F[Unit] = {
    BlazeServerBuilder[F]
      .bindHttp(port = 9000, host = "localhost")
      .withHttpApp(routes())
      .serve
      .compile
      .drain
  }
}
