package com.aahsk.chromino.http.game

import java.util.concurrent.atomic.AtomicReference

import fs2.Pipe
import cats.data.{EitherT, OptionT}
import cats.implicits._
import cats.effect.Concurrent
import io.circe.parser._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.websocket.WebSocketFrame
import GameSubroute.GameSubroute
import com.aahsk.chromino.protocol.{
  Connection,
  GameRequest,
  GameResponse,
  Message => GameMessage
}
import com.aahsk.chromino.protocol.Message._
import org.http4s.server.websocket.WebSocketBuilder
import cats.effect.Concurrent
import cats.implicits._
import com.aahsk.chromino.protocol.meta.error.{
  MessageParseError,
  MissingRouteError
}
import com.aahsk.chromino.protocol.meta.error.MessageParseError.OutgoingMessageParseError
import org.http4s.Response
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Queue
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Pipe

case class GameRoute[F[_]: Concurrent]() {
  def createUpstreamRoutes(
      connectionRef: AtomicReference[Connection]
  ): GameSubroute[F] = {
    val authRoutes: GameSubroute[F] =
      new AuthRoute[F](connectionRef).createRoutes();

    authRoutes <+> authRoutes
  }

  /**
    * Author's note:
    *  Somewhat sure that I'm ignoring whether a request has been split
    *  into multiple chunks by doing the `Text(message,_)`
    */
  def connectionPipe(
      connectionRef: AtomicReference[Connection]
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
            message => route(GameRequest(message, connectionRef))
          )
          .merge
          .getOrElse(MissingRouteError.toOutgoing())
          .map(response =>
            WebSocketFrame.Text(response.message.asJson.noSpaces)
          )
    }
  }

  private def gameRoute(): HttpRoutes[F] = {
    val connectionRef = new AtomicReference(Connection(None))
    val pipe = connectionPipe(connectionRef)
    HttpRoutes.of[F] {
      case GET -> Root / "game" =>
        for {
          // Unbounded queue which can OOM only when messages aren't processed quickly enough
          queue <- Queue.unbounded[F, WebSocketFrame]
          response <- WebSocketBuilder[F].build(
            // Sink, where the incoming WebSocket messages from the client are pushed to.
            receive = queue.enqueue,
            // Outgoing stream of WebSocket messages to send to the client.
            send = queue.dequeue.through(pipe)
          )
        } yield response
    }
  }

  def createRoutes(): HttpRoutes[F] = {
    gameRoute()
  }
}
