package com.aahsk.chromino.http

import cats.implicits._
import cats.{Applicative, Monad}
import cats.effect.{ConcurrentEffect, Sync}
import cats.effect.concurrent.Ref
import com.aahsk.chromino.logic.GameController
import com.aahsk.chromino.protocol.{GameState, Message}
import com.aahsk.chromino.protocol.Message._
import fs2.Pipe
import fs2.Stream
import fs2.concurrent.Queue
import org.http4s.HttpRoutes
import org.http4s.dsl.io.{+&, ->, /, :?, GET, QueryParamDecoderMatcher, Root}
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import com.aahsk.chromino.protocol.Codecs._
import io.circe.Error
import io.circe.parser._
import io.circe.syntax._

case class GameRoute[F[_]: ConcurrentEffect: Monad](
  gameControllers: Ref[F, List[GameController[F]]]
) {
  def processMessage(
    gameName: String,
    nick: String,
    maybeMessage: Either[Error, Message],
    controllers: List[GameController[F]]
  ): (List[GameController[F]], F[Message]) =
    (maybeMessage, controllers.find(_.game.name == gameName)) match {
      case (Right(message), Some(controller)) => (controllers, controller.process(nick, message))
      case (Left(decodeError), _) =>
        (controllers, Applicative[F].pure(MessageParseError(decodeError.getMessage)))
      case (_, None) => (controllers, Applicative[F].pure(GameNotFoundError()))
    }

  def joinGame(
    gameName: String,
    expectedPlayerCount: Int,
    nick: String,
    toClient: Queue[F, Message],
    controllers: List[GameController[F]]
  ): (List[GameController[F]], F[Unit]) =
    controllers.find(_.game.name == gameName) match {
      case None =>
        val controller = GameController.create(
          gameName,
          expectedPlayerCount,
          nick,
          toClient
        )
        (
          controllers :+ controller,
          toClient.enqueue1(GameStateMessage(GameState.of(controller.game, nick)))
        )
      case Some(controller) =>
        (
          controllers,
          controller.joinPlayer(nick, toClient) *>
            toClient.enqueue1(GameStateMessage(GameState.of(controller.game, nick)))
        )
    }

  def fromClient(
    clientOutQueue: Queue[F, Message],
    gameName: String,
    nick: String
  ): Pipe[F, WebSocketFrame, Unit] =
    _.evalMap { case WebSocketFrame.Text(text, _) =>
      for {
        maybeMessage <- Monad[F].pure(decode[Message](text))
        result <- gameControllers
          .modify(controllers => processMessage(gameName, nick, maybeMessage, controllers))
          .flatten
        _ <- clientOutQueue.enqueue1(result)
      } yield ()
    }

  def toClient(clientOutQueue: Queue[F, Message]): Stream[F, WebSocketFrame] =
    clientOutQueue.dequeue.through(
      _.evalMap(m => Monad[F].pure(WebSocketFrame.Text(m.asJson.spaces2)))
    )

  def createRoute(): HttpRoutes[F] = {
    object Nick                extends QueryParamDecoderMatcher[String]("nick")
    object ExpectedPlayerCount extends QueryParamDecoderMatcher[Int]("expectedPlayerCount")
    HttpRoutes.of[F] {
      case GET -> Root / "game" / gameName :? Nick(nick) +& ExpectedPlayerCount(
            expectedPlayerCount
          ) =>
        for {
          clientOutQueue <- Queue.unbounded[F, Message]
          clientOut = toClient(clientOutQueue)
          clientIn  = fromClient(clientOutQueue, gameName, nick)
          _ <- gameControllers
            .modify(controllers =>
              joinGame(
                gameName,
                expectedPlayerCount,
                nick,
                clientOutQueue,
                controllers
              )
            )
            .flatten
          response <- WebSocketBuilder[F].build(
            clientOut,
            clientIn
          )
        } yield response
    }
  }
}

object GameRoute {
  def create[F[_]: ConcurrentEffect](): F[HttpRoutes[F]] =
    for {
      gameControllers <- Ref.of[F, List[GameController[F]]](List[GameController[F]]())
      gameRoute = GameRoute(gameControllers).createRoute()
    } yield gameRoute
}
