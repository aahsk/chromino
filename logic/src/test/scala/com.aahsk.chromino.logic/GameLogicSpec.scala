package com.aahsk.chromino.protocol

import org.scalatest.freespec.AnyFreeSpec
import com.aahsk.chromino.domain.{BoardChromino, Chromino, Position}
import com.aahsk.chromino.domain.Chromino._
import com.aahsk.chromino.domain.Rotation._
import com.aahsk.chromino.logic.GameLogic

class GameLogicSpec extends AnyFreeSpec {
  "random chromino should pick something out" in {
    assert(
      GameLogic
        .pickRandomChromino(List(RegularRRR, RegularRRP), List(RegularRRR))
        .contains((List(RegularRRP), RegularRRR))
    )
  }

  "random chrominos should pick something out" in {
    val reality =
      GameLogic
        .pickRandomChrominos(
          List(RegularRRR, RegularRRP, RegularRRY),
          2,
          List(RegularRRR, RegularRRP)
        ) match { case (bag, picks) => (bag.toSet, picks.toSet) }
    val expectations = (Set(RegularRRY), Set(RegularRRR, RegularRRP))
    assert(reality == expectations)
  }

  "random player initialization should pick something out" in {
    val (postBag, picks) =
      GameLogic.pickPlayerInitChrominos(Chromino.values.toList, List("joe", "bob", "alice"))
    assert(postBag.size == (Chromino.values.toList.size - 8 * 3))
    assert(picks.size == 3)
    assert(picks.toList.map(_._2).forall(_.size == 8))
  }

  "game should be startable" in {
    val newGame          = GameLogic.createGame("game", "a", 3)
    val bJoinedGame      = GameLogic.joinPlayer(newGame, "b")
    val cJoinedGame      = GameLogic.joinPlayer(bJoinedGame, "c")
    val cJoinedGameAgain = GameLogic.joinPlayer(cJoinedGame, "c")
    val game             = cJoinedGameAgain

    assert(!game.waitingPlayers, "waiting players")
    assert(game.players.size == 3, "three players in game")
    assert(game.playerChrominos.size == 3, "three player chromino sets")
    assert(
      game.playerChrominos.forall { case (_, chrominos) => chrominos.size == 8 },
      "every chromino set has 8 pieces"
    )
  }

  "added piece overlap is validated properly" in {
    val existing = List(BoardChromino(Chromino.RegularRBP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularRBP, Position(1, 0), N)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Left(
        "Chromino overlaps existing board chrominos"
      )
    )
  }

  "added piece color mismatch is validated properly" in {
    val existing = List(BoardChromino(Chromino.RegularRBP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularRBP, Position(-1, -1), N)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Left(
        "Chromino colors don't match colors of nearby chrominos"
      )
    )
  }

  "added piece neighbour existance is validated properly" in {
    val existing = List(BoardChromino(Chromino.RegularRBP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularRBP, Position(50, 50), N)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Left(
        "No neighbouring chrominos present"
      )
    )
  }

  "added piece requiring at least two neighbours is validated properly" in {
    val existing = List(BoardChromino(Chromino.RegularRPB, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularRBP, Position(0, -2), W)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Left(
        "At least two neighbouring chrominos required"
      )
    )
  }

  "added piece which is valid is validated properly (regulars only)" in {
    val existing = List(BoardChromino(Chromino.RegularRBR, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularBRP, Position(-1, -1), S)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Right(added)
    )
  }

  "added piece which is valid is validated properly (one wildcard)" in {
    val existing = List(BoardChromino(Chromino.WildcardYXP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularRPB, Position(1, -1), N)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Right(added)
    )
  }

  "added piece which is valid is validated properly (identical)" in {
    val existing = List(BoardChromino(Chromino.WildcardYXP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.WildcardYXP, Position(0, -1), N)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Right(added)
    )
  }

  "added piece which is valid is validated properly (almost identical)" in {
    val existing = List(BoardChromino(Chromino.WildcardYXP, Position(0, 0), N))
    val added    = BoardChromino(Chromino.RegularPBY, Position(0, -1), S)
    assert(
      GameLogic.validateBoardChromino(existing, added) == Right(added)
    )
  }
}
