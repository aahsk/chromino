package com.aahsk.chromino.domain

case class Game(
  name: String,
  board: Board,
  creatorNick: String,
  players: List[User],
  activePlayerIndex: Int,
  winnerIndex: Option[Int],
  playerChrominos: Map[String, Chromino],
  maxPlayerCount: Int,
  waitingPlayers: Boolean
)
