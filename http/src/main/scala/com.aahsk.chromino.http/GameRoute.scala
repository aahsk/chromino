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
import org.http4s.dsl.io.{->, /, GET, Root}
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import com.aahsk.chromino.protocol.Codecs._
import io.circe.Error
import io.circe.parser._
import io.circe.syntax._

case class GameRoute[F[_]: ConcurrentEffect: Monad](
  gameControllers: Ref[F, F[List[GameController[F]]]]
) {
  def controllerMessageProcess(
    gameName: String,
    nick: String,
    maybeMessage: Either[Error, Message],
    controllersF: F[List[GameController[F]]]
  ): (F[List[GameController[F]]], F[Message]) = {
    val modification: F[(List[GameController[F]], Message)] = for {
      controllers <- controllersF
      maybeController = controllers.find(_.game.name == gameName)
      result <- (maybeMessage, maybeController) match {
        case (Right(message), Some(controller)) => controller.process(nick, message)
        case (Left(decodeError), _) =>
          Applicative[F].pure(MessageParseError(decodeError.getMessage))
        case (_, None) => Applicative[F].pure(GameNotFoundError())
      }
    } yield (controllers, result)
    (modification.map(_._1), modification.map(_._2))
  }

  def fromClient(
    clientOutQueue: Queue[F, WebSocketFrame],
    gameName: String,
    nick: String
  ): Pipe[F, WebSocketFrame, Unit] =
    _.evalMap { case WebSocketFrame.Text(text, _) =>
      for {
        maybeMessage <- Monad[F].pure(decode[Message](text))
        result <- gameControllers
          .modify(controllers =>
            controllerMessageProcess(gameName, nick, maybeMessage, controllers)
          )
          .flatten
        response = WebSocketFrame.Text(result.asJson.spaces2)
        _ <- clientOutQueue.enqueue1(response)
      } yield ()
    }

  def toClient(clientOutQueue: Queue[F, WebSocketFrame]): Stream[F, WebSocketFrame] =
    clientOutQueue.dequeue

  def createRoute(): HttpRoutes[F] = {
    // Todo Add player nick in query params
    HttpRoutes.of[F] { case GET -> Root / "game" / gameName =>
      for {
        clientOutQueue <- Queue.unbounded[F, WebSocketFrame]
        clientOut = toClient(clientOutQueue)
        clientIn  = fromClient(clientOutQueue, gameName, "nick")
        // Todo - add game controller and configure player queue in controller
//        _ <- gameControllers.modify(controllersF => {
//          for {
//            controllers <- controllersF
//            maybeController = controllers.find(_.game.name == gameName)
//          } (controllers, 2)
//        })
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
