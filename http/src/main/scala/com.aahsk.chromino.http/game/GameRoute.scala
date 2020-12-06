package com.aahsk.chromino.http.game

import java.util.concurrent.atomic.AtomicReference

import cats.data.OptionT
import org.http4s._
import org.http4s.dsl.io._
import GameSubroute.GameSubroute
import cats.Applicative
import com.aahsk.chromino.protocol.{Connection, Message => GameMessage}
import com.aahsk.chromino.protocol.Message._
import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import cats.implicits._
import com.aahsk.chromino.persistance.Database.Database
import com.aahsk.chromino.protocol.meta.error.{
  MessageParseError,
  MissingRouteError
}
import com.aahsk.chromino.protocol.meta.error.MessageParseError.OutgoingMessageParseError
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Queue
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Pipe

case class GameRoute[F[_]: ConcurrentEffect](database: Database[F]) {
  def createUpstreamRoutes(
      connectionRef: Ref[F, Connection]
  ): GameSubroute[F] = {
    val authRoutes: GameSubroute[F] =
      new AuthRoute[F](database, connectionRef).createRoutes();

    authRoutes <+> authRoutes
  }

  /**
    * Author's note:
    *  Somewhat sure that I'm ignoring whether a request has been split
    *  into multiple chunks by doing the `Text(message,_)`
    */
  def connectionPipe(
      connectionRef: Ref[F, Connection]
  ): Pipe[F, WebSocketFrame, WebSocketFrame] = {
    import io.circe.parser._
    import io.circe.syntax._
    val route = createUpstreamRoutes(connectionRef)

    _.evalMap {
      case WebSocketFrame.Text(text, _) =>
        decode[GameMessage](text)
          .bimap(
            error =>
              OptionT.pure[F](
                MessageParseError.toOutgoing(
                  OutgoingMessageParseError(error.toString)
                )
              ),
            message => route(message)
          )
          .merge
          .getOrElse(MissingRouteError.toOutgoing())
          .map(message => WebSocketFrame.Text(message.asJson.noSpaces))
    }
  }

  private def gameRoute(): HttpRoutes[F] = {
    HttpRoutes.of[F] {
      case GET -> Root / "game" =>
        for {
          connectionRef <- Ref.of[F, Connection](Connection(None))
          pipe <- Applicative[F].pure(connectionPipe(connectionRef))
          queue <- Queue.unbounded[F, WebSocketFrame]
          response <- WebSocketBuilder[F].build(
            receive = queue.enqueue,
            send = queue.dequeue.through(pipe)
          )
        } yield response
    }
  }

  def createRoutes(): HttpRoutes[F] = {
    gameRoute()
  }
}
