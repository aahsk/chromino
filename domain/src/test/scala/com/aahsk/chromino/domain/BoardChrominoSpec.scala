package com.aahsk.chromino.domain

import com.aahsk.chromino.domain.ChrominoColor._
import com.aahsk.chromino.domain.Rotation._
import org.scalatest.freespec.AnyFreeSpec

class BoardChrominoSpec extends AnyFreeSpec {
  "chromino should be processable into colored board squares" in {
    val chromino = Chromino.RegularRBY
    val boardChromino = BoardChromino(
      chromino,
      Position(2, 2),
      W
    )
    val squares = boardChromino.squared()
    assert(squares.contains(ChrominoSquare(Position(2, 1), R)))
    assert(squares.contains(ChrominoSquare(Position(2, 2), B)))
    assert(squares.contains(ChrominoSquare(Position(2, 3), Y)))
  }
}
