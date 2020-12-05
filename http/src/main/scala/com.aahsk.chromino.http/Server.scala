package com.aahsk.chromino.http

import cats.implicits._
import cats.{Defer, Monad}
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import com.aahsk.chromino.http.game.GameRoute
import com.aahsk.chromino.persistance.Database.Database

import scala.concurrent.ExecutionContext

object Server {
  def routes[F[_]: Monad: Defer: ConcurrentEffect](
      database: Database[F]
  ): HttpApp[F] = {
    val gameRoutes: HttpRoutes[F] = new GameRoute[F](database).createRoutes();
    val healthRoutes: HttpRoutes[F] = new HealthRoute[F]().createRoutes()

    gameRoutes <+> healthRoutes
  }.orNotFound

  def run[F[_]: Timer: ContextShift: ConcurrentEffect](
      database: Database[F]
  )(implicit
      EC: ExecutionContext
  ): F[Unit] = {
    BlazeServerBuilder[F](EC)
      .bindHttp(port = 9000, host = "localhost")
      .withHttpApp(routes(database))
      .serve
      .compile
      .drain
  }
}
