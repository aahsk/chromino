package com.aahsk.chromino.domain

case class Board(bag: List[Chromino], pieces: List[BoardChromino])
object Board {
  def empty() = Board(Chromino.values.toList, List())
}
