package com.aahsk.chromino.domain

import org.scalatest.flatspec.AnyFlatSpec
import com.aahsk.chromino.domain.Color._
import com.aahsk.chromino.domain.Rotation._

class BoardChrominoSpec extends AnyFlatSpec {
  "chromino" should "be processable into colored board squares" in {
    val chromino = Regular(Red, Blue, Yellow)
    val boardChromino = BoardChromino(
      chromino,
      Position(2, 2),
      Deg90
    )
    val squares = boardChromino.squared()
    assert(squares.contains(Square(Position(2, 1), Red)))
    assert(squares.contains(Square(Position(2, 2), Blue)))
    assert(squares.contains(Square(Position(2, 3), Yellow)))
  }
}
