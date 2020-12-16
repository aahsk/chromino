package com.aahsk.chromino.protocol

import org.scalatest.freespec.AnyFreeSpec
import io.circe.parser._
import com.aahsk.chromino.protocol.Codecs._
import com.aahsk.chromino.protocol.Message._
import com.aahsk.chromino.domain.Chromino
import com.aahsk.chromino.domain.Chromino._
import io.circe.{Json, JsonObject}
import cats.implicits._
import com.aahsk.chromino.logic.GameLogic

class GameLogicSpec extends AnyFreeSpec {
  "random chromino should pick something out" in {
    assert(
      GameLogic.pickRandomChromino(List(RegularRRR, RegularRRP), List(RegularRRR)) ==
        Some((List(RegularRRP),RegularRRR))
    )
  }

  "random chrominos should pick something out" in {
    val reality =
      GameLogic
        .pickRandomChrominos(List(RegularRRR, RegularRRP, RegularRRY), 2, List(RegularRRR, RegularRRP))
        match { case (bag, picks) => (bag.toSet, picks.toSet) }
    val expectations = (Set(RegularRRY), Set(RegularRRR, RegularRRP))
    assert(reality == expectations)
  }

  "random player initialization should pick something out" in {
    val (postBag, picks) = GameLogic.pickPlayerInitChrominos(Chromino.values.toList, List("joe", "bob", "alice"))
    assert(postBag.size == (Chromino.values.toList.size - 8 * 3))
    assert(picks.size == 3)
    assert(picks.toList.map(_._2).forall(_.size == 8))
  }

  "game should be startable" in {
    val newGame = GameLogic.createGame("game", "a", 3)
    val bJoinedGame = GameLogic.joinPlayer(newGame, "b")
    val cJoinedGame = GameLogic.joinPlayer(bJoinedGame, "c")
    val cJoinedGameAgain = GameLogic.joinPlayer(bJoinedGame, "c")
    val game = cJoinedGameAgain

    assert(!game.waitingPlayers, "waiting players")
    assert(game.players.size == 3, "three players in game")
    assert(game.playerChrominos.size == 3, "three player chromino sets")
    assert(game.playerChrominos.forall { case (_, chrominos) => chrominos.size == 8 }, "every chromino set has 8 pieces")
  }
}
