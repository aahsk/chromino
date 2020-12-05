package com.aahsk.chromino.http

import cats.effect.Sync
import cats.implicits._
import org.http4s._
import org.http4s.dsl.Http4sDsl

class HealthRoute[F[_]: Sync] extends Http4sDsl[F] {
  def livenessRoute(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "health" / "liveness" => Ok("OK!")
    }

  def readinessRoute(): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "health" / "readiness" => Ok("OK!")
    }

  def createRoutes(): HttpRoutes[F] = {
    livenessRoute() <+> readinessRoute()
  }
}
