package com.aahsk.chromino.logic

import cats.implicits._
import cats.Monad
import cats.effect.Sync
import fs2.concurrent.Queue
import com.aahsk.chromino.domain.{Board, Chromino, Game, User}
import com.aahsk.chromino.protocol.{GameState, Message}
import com.aahsk.chromino.protocol.Message.{
  ConnectionMigrated,
  GameStateMessage,
  PlayerJoined,
  Pong
}

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
    // Construct user
    val user = User(nick)

    // Add user to game
    game.players.find(_.nick == nick) match {
      case None => game = game.copy(players = game.players :+ user)
    }

    // If appropriate, start game
    if (game.players.length == game.expectedPlayerCount) {
      game = game.copy(waitingPlayers = false)
    }

    // Store old connection if exists
    val oldConnection = toPlayers.get(user.nick)

    // Configure new connection
    toPlayers = toPlayers.updated(user.nick, toNick)

    // Induce effects on world
    for {
      // Inform old connection that it's old
      _ <- oldConnection match {
        case Some(oldQueue) => oldQueue.enqueue1(ConnectionMigrated())
        case None           => Monad[F].pure(())
      }
      // Broadcast player joined
      _ <- broadcastToNick[PlayerJoined](_ => PlayerJoined(user))
      // If game started, broadcast its state
      _ <-
        if (game.waitingPlayers) {
          Monad[F].pure(())
        } else {
          broadcastToNick[GameStateMessage](nick => GameStateMessage(GameState.of(game, nick)))
        }
    } yield ()
  }
}

object GameController {
  def create[F[_]: Sync](
    gameName: String,
    expectedPlayerCount: Int,
    creatorNick: String,
    toCreator: Queue[F, Message]
  ): GameController[F] = {
    val game = Game(
      name = gameName,
      board = Board.empty(),
      creatorNick = creatorNick,
      players = List(User(creatorNick)),
      activePlayerIndex = 0,
      winnerIndex = None,
      playerChrominos = Map[String, List[Chromino]](),
      expectedPlayerCount = expectedPlayerCount,
      waitingPlayers = true
    )
    val toPlayers = Map[String, Queue[F, Message]](
      creatorNick -> toCreator
    )
    new GameController[F](game, toPlayers)
  }
}
