package com.aahsk.chromino.boot

import cats.effect.{ExitCode, IO, IOApp}
import com.aahsk.chromino.http.Server
import scala.concurrent.ExecutionContext.global

object Chromino extends IOApp {
  override def run(
    args: List[String]
  ): IO[ExitCode] = {
    for {
      _ <- Server.run[IO](global)
    } yield ExitCode.Success
  }
}
