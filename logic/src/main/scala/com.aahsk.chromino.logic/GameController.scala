package com.aahsk.chromino.logic

import scala.util.Random
import cats.implicits._
import cats.Monad
import cats.effect.Sync
import fs2.concurrent.Queue
import com.aahsk.chromino.domain.{Board, BoardChromino, Chromino, Game, Position, Rotation, User}
import com.aahsk.chromino.protocol.{GameState, Message}
import com.aahsk.chromino.protocol.Message.{
  ConnectionMigrated,
  GameStateMessage,
  PlayerJoined,
  Pong
}

/** Author's note: This could probably be made functional by not using an effectful
  *  queue, but rather by using to-be-sent-out messages in method returns
  */
class GameController[F[_]: Sync](
  var game: Game,
  var toPlayers: Map[String, Queue[F, Message]]
) {
  def process(nick: String, message: Message): F[Message] = Monad[F].pure(Pong())
  def broadcastToNick[M <: Message](getMessageByNick: String => M): F[Unit] = {
    toPlayers.toList
      .map { case (nick, queue) =>
        queue.enqueue1(getMessageByNick(nick))
      }
      .sequence
      .void
  }

  def joinPlayer(nick: String, toNick: Queue[F, Message]): F[Unit] = {
    // Process logical changes
    game = GameLogic.joinPlayer(game, nick)

    // Store old connection if exists
    val oldConnection = toPlayers.get(nick)

    // Configure new connection
    toPlayers = toPlayers.updated(nick, toNick)

    // Induce effects on world
    for {
      // Inform old connection that it's old
      _ <- oldConnection match {
        case Some(oldQueue) => oldQueue.enqueue1(ConnectionMigrated())
        case None           => Monad[F].pure(())
      }
      // Broadcast player joined
      _ <- broadcastToNick[PlayerJoined](_ => PlayerJoined(game.players))
      // If game started, broadcast its state
      _ <-
        if (game.waitingPlayers) {
          Monad[F].pure(())
        } else {
          broadcastToNick[GameStateMessage](nick => GameStateMessage(GameState.of(game, nick)))
        }
    } yield ()
  }

  def disconnectPlayer(nick: String): F[Unit] = {
    toPlayers = toPlayers.removed(nick)
    Monad[F].pure()
  }
}

object GameController {
  def create[F[_]: Sync](
    gameName: String,
    expectedPlayerCount: Int,
    creatorNick: String,
    toCreator: Queue[F, Message]
  ): GameController[F] = {
    val game = GameLogic.createGame(gameName, creatorNick, expectedPlayerCount)
    val toPlayers = Map[String, Queue[F, Message]](
      creatorNick -> toCreator
    )
    new GameController[F](game, toPlayers)
  }
}
