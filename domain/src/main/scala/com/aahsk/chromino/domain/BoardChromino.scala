package com.aahsk.chromino.domain

case class BoardChromino(
    chromino: Chromino,
    centerPosition: Position,
    centerRotation: Rotation
) {
  def squared(): List[Square] = {
    import Rotation._

    val a = Square(
      Position(
        centerRotation match {
          case Deg0   => centerPosition.x - 1
          case Deg90  => centerPosition.x
          case Deg180 => centerPosition.x + 1
          case Deg270 => centerPosition.x
        },
        centerRotation match {
          case Deg0   => centerPosition.y
          case Deg90  => centerPosition.y - 1
          case Deg180 => centerPosition.y
          case Deg270 => centerPosition.y + 1
        }
      ),
      chromino.leftColor
    )

    val b = Square(centerPosition, chromino.centerColor)

    val c = Square(
      Position(
        centerRotation match {
          case Deg0   => centerPosition.x + 1
          case Deg90  => centerPosition.x
          case Deg180 => centerPosition.x - 1
          case Deg270 => centerPosition.x
        },
        centerRotation match {
          case Deg0   => centerPosition.y
          case Deg90  => centerPosition.y + 1
          case Deg180 => centerPosition.y
          case Deg270 => centerPosition.y - 1
        }
      ),
      chromino.rightColor
    )

    List(a, b, c)
  }
}
