package com.aahsk.chromino.domain

case class Board(chrominos: List[Chromino with CenterPositioned with CenterRotated])

case class Square(position: Position, color: Color)

sealed trait Destructable {
  def squares(): List[Square]
}

case class ChrominoPiece(chromino: Chromino, centerPosition: Position, rotation: Rotation ) extends Piece {
  import Rotation._

  def squares(): List[Square] = {
    val a = Square(
      Position(
        rotation match {
          case Deg0   => centerPosition.x - 1
          case Deg90  => centerPosition.x
          case Deg180 => centerPosition.x + 1
          case Deg270 => centerPosition.x
        },
        rotation match {
          case Deg0   => centerPosition.y
          case Deg90  => centerPosition.y - 1
          case Deg180 => centerPosition.y
          case Deg270 => centerPosition.y + 1
        }
      ),
      leftColor
    )

    val b = Square(centerX, centerY, center)

    val c = Square(
      rotation match {
        case Deg0   => centerX + 1
        case Deg90  => centerX
        case Deg180 => centerX - 1
        case Deg270 => centerX
      },
      rotation match {
        case Deg0   => centerY
        case Deg90  => centerY + 1
        case Deg180 => centerY
        case Deg270 => centerY - 1
      },
      center
    )

    List(a, b, c)
  }
}
