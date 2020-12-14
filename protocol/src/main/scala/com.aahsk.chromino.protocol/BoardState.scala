package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{Board, BoardChromino}

final case class BoardState(pieces: List[BoardChromino])
object BoardState {
  def of(board: Board): BoardState = BoardState(board.pieces)
}
