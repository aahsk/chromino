package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{Board, BoardChromino}

final case class BoardState(pieces: List[BoardChromino])
object BoardState {
  def ofBoard(board: Board): Unit = {
    BoardState(board.pieces)
  }
}
