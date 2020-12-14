package com.aahsk.chromino.logic

import cats.implicits._
import cats.Monad
import fs2.concurrent.Queue
import com.aahsk.chromino.domain.{Board, Chromino, Game, User}
import com.aahsk.chromino.protocol.Message
import com.aahsk.chromino.protocol.Message.{ConnectionMigrated, Pong}

class GameController[F[_]: Monad](
  var game: Game,
  var toPlayers: Map[String, Queue[F, Message]]
) {
  def process(nick: String, message: Message): F[Message] = Monad[F].pure(Pong())
  def joinPlayer(nick: String, toNick: Queue[F, Message]): F[Unit] = {
    val oldToPlayer = toPlayers.get(nick)
    toPlayers = toPlayers.updated(nick, toNick)

    oldToPlayer match {
      case Some(oldQueue) => oldQueue.enqueue1(ConnectionMigrated()).void
      case None           => Monad[F].pure(())
    }
  }
}

object GameController {
  def create[F[_]: Monad](
    gameName: String,
    maxPlayerCount: Int,
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
      playerChrominos = Map[String, Chromino](),
      maxPlayerCount = maxPlayerCount,
      waitingPlayers = true
    )
    val toPlayers = Map[String, Queue[F, Message]](
      creatorNick -> toCreator
    )
    new GameController[F](game, toPlayers)
  }
}
