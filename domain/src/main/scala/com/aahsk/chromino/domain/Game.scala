package com.aahsk.chromino.domain

import java.time.LocalTime

import User._
import Game._

case class Game(
    id: Option[GameID],
    name: String,
    board: Board,
    creator: UserID,
    currentTurnChromino: Option[Chromino],
    currentTurnPlayer: Option[UserID],
    players: List[UserID],
    maxPlayerCount: Int,
    winner: Option[UserID],
    createdAt: LocalTime,
    startedAt: Option[LocalTime],
    finishedAt: Option[LocalTime]
)

object Game {
  type GameID = Int

  implicit val indexedGame: Indexed[Game] =
    new Indexed[Game] {
      override def updatedID(entity: Game, id: GameID): Game =
        entity.copy(id = Some(id))
      override def id(entity: Game): Option[GameID] = entity.id
    }
}
