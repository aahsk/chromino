package com.aahsk.chromino.http

import cats.implicits._
import cats.{Applicative, Monad}
import cats.effect.ConcurrentEffect
import cats.effect.concurrent.Ref
import com.aahsk.chromino.logic.GameController
import com.aahsk.chromino.protocol.Message
import com.aahsk.chromino.protocol.Message._
import fs2.Pipe
import fs2.Stream
import fs2.concurrent.Queue
import org.http4s.HttpRoutes
import org.http4s.dsl.io.{:?, +&, ->, /, GET, QueryParamDecoderMatcher, Root}
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import com.aahsk.chromino.protocol.Codecs._
import io.circe.Error
import io.circe.parser._
import io.circe.syntax._

case class GameRoute[F[_]: ConcurrentEffect: Monad](
  gameControllers: Ref[F, F[List[GameController[F]]]]
) {
  def effectModifySplit[A, B](modification: F[(A, B)]): (F[A], F[B]) =
    (modification.map(_._1), modification.map(_._2))

  def processMessage(
    gameName: String,
    nick: String,
    maybeMessage: Either[Error, Message],
    controllersF: F[List[GameController[F]]]
  ): F[(List[GameController[F]], Message)] =
    for {
      controllers <- controllersF
      maybeController = controllers.find(_.game.name == gameName)
      result <- (maybeMessage, maybeController) match {
        case (Right(message), Some(controller)) => controller.process(nick, message)
        case (Left(decodeError), _) =>
          Applicative[F].pure(MessageParseError(decodeError.getMessage))
        case (_, None) => Applicative[F].pure(GameNotFoundError())
      }
    } yield (controllers, result)

  def joinGame(
    gameName: String,
    maxPlayerCount: Int,
    nick: String,
    toClient: Queue[F, Message],
    controllersF: F[List[GameController[F]]]
  ): F[(List[GameController[F]], Unit)] =
    for {
      controllers <- controllersF
      maybeController = controllers.find(_.game.name == gameName)
      newControllers <- maybeController match {
        case None =>
          Monad[F].pure(
            controllers :+ GameController.create(
              gameName,
              maxPlayerCount,
              nick,
              toClient
            )
          )
        case Some(controller) =>
          controller.joinPlayer(nick, toClient).map(_ => controllers)
      }
    } yield (newControllers, ())

  def fromClient(
    clientOutQueue: Queue[F, Message],
    gameName: String,
    nick: String
  ): Pipe[F, WebSocketFrame, Unit] =
    _.evalMap { case WebSocketFrame.Text(text, _) =>
      for {
        maybeMessage <- Monad[F].pure(decode[Message](text))
        result <- gameControllers
          .modify(controllers =>
            effectModifySplit(processMessage(gameName, nick, maybeMessage, controllers))
          )
          .flatten
        _ <- clientOutQueue.enqueue1(result)
      } yield ()
    }

  def toClient(clientOutQueue: Queue[F, Message]): Stream[F, WebSocketFrame] =
    clientOutQueue.dequeue.through(
      _.evalMap(m => Monad[F].pure(WebSocketFrame.Text(m.asJson.spaces2)))
    )

  def createRoute(): HttpRoutes[F] = {
    object Nick           extends QueryParamDecoderMatcher[String]("nick")
    object MaxPlayerCount extends QueryParamDecoderMatcher[Int]("maxPlayerCount")
    HttpRoutes.of[F] {
      case GET -> Root / "game" / gameName :? Nick(nick) +& MaxPlayerCount(maxPlayerCount) =>
        for {
          clientOutQueue <- Queue.unbounded[F, Message]
          clientOut = toClient(clientOutQueue)
          clientIn  = fromClient(clientOutQueue, gameName, nick)
          _ <- gameControllers.modify(controllersF =>
            effectModifySplit(
              joinGame(
                gameName,
                maxPlayerCount,
                nick,
                clientOutQueue,
                controllersF
              )
            )
          )
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
      gameControllers <- Ref
        .of[F, F[List[GameController[F]]]](
          Applicative[F].pure(List[GameController[F]]())
        )
      gameRoute = GameRoute(gameControllers).createRoute()
    } yield gameRoute
}
