package com.aahsk.chromino.domain

import java.time.LocalTime
import User._
import Game._

case class Game(
    id: GameID,
    name: String,
    bag: Bag,
    pieces: Map[UserID, Piece],
    users: List[User],
    winner: Option[UserID],
    createdAt: LocalTime,
    startedAt: Option[LocalTime]
)

object Game {
  type GameID = Int
  def ofStart(): Game = ???
}
