package com.aahsk.chromino.boot

import cats.effect.{ExitCode, IO, IOApp}
import com.aahsk.chromino.http.Server
import com.aahsk.chromino.persistance.{Data, Database}
import scala.concurrent.ExecutionContext.Implicits.global

object App extends IOApp {
  override def run(
      args: List[String]
  ): IO[ExitCode] = {
    for {
      database <- Database.empty[IO]()
      server <- Server.run[IO](database)
    } yield ExitCode.Success
  }
}
