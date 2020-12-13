package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{Chromino, User}

final case class GameState(
  name: String,
  board: BoardState,
  creatorNick: String,
  players: List[User],
  activePlayerIndex: String,
  winnerIndex: Option[Int],
  requesterChrominos: List[Chromino],
  maxPlayerCount: Int,
  waitingPlayers: Boolean
)
