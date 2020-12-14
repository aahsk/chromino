package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{Chromino, Game, User}

final case class GameState(
  name: String,
  board: BoardState,
  creatorNick: String,
  players: List[User],
  activePlayerIndex: Int,
  winnerIndex: Option[Int],
  requesterChrominos: List[Chromino],
  expectedPlayerCount: Int,
  waitingPlayers: Boolean
)
object GameState {
  def of(game: Game, requesterNick: String): GameState = {
    import game._
    GameState(
      name = name,
      board = BoardState.of(board),
      creatorNick = creatorNick,
      players = players,
      activePlayerIndex = activePlayerIndex,
      winnerIndex = winnerIndex,
      requesterChrominos = playerChrominos
        .find { case (nick, _) => nick == requesterNick }
        .map { case (_, chrominos) => chrominos }
        .getOrElse(List[Chromino]()),
      expectedPlayerCount = expectedPlayerCount,
      waitingPlayers = waitingPlayers
    )
  }
}
