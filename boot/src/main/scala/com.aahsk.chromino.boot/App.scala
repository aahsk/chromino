package com.aahsk.chromino.boot

import cats.effect.{ExitCode, IO, IOApp}
import com.aahsk.chromino.http.Server
import scala.concurrent.ExecutionContext.Implicits.global

object App extends IOApp {
  override def run(
      args: List[String]
  ): IO[ExitCode] =
    Server
      .run[IO]()
      .as(ExitCode.Success)
}
