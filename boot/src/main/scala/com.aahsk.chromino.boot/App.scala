package com.aahsk.chromino.boot

import cats.effect.{ExitCode, IO, IOApp}
import com.aahsk.chromino.http.Server

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object App extends IOApp {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  override def run(
      args: List[String]
  ): IO[ExitCode] =
    Server
      .run[IO]()
      .as(ExitCode.Success)
}
