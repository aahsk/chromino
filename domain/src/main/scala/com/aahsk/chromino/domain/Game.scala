package com.aahsk.chromino.domain

import java.time.LocalTime
import User._
import Game._

case class Game(
    id: GameID,
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
}
