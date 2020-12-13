package com.aahsk.chromino.domain

import scala.scalajs.js.annotation._

@JSExportTopLevel("BoardChromino")
case class BoardChromino(
  chromino: Chromino,
  centerPosition: Position,
  centerRotation: Rotation
) {
  def squared(): List[ChrominoSquare] = {
    import Rotation._

    val a = ChrominoSquare(
      Position(
        centerRotation match {
          case N => centerPosition.x - 1
          case W => centerPosition.x
          case S => centerPosition.x + 1
          case E => centerPosition.x
        },
        centerRotation match {
          case N => centerPosition.y
          case W => centerPosition.y - 1
          case S => centerPosition.y
          case E => centerPosition.y + 1
        }
      ),
      chromino.leftColor
    )

    val b = ChrominoSquare(centerPosition, chromino.centerColor)

    val c = ChrominoSquare(
      Position(
        centerRotation match {
          case N => centerPosition.x + 1
          case W => centerPosition.x
          case S => centerPosition.x - 1
          case E => centerPosition.x
        },
        centerRotation match {
          case N => centerPosition.y
          case W => centerPosition.y + 1
          case S => centerPosition.y
          case E => centerPosition.y - 1
        }
      ),
      chromino.rightColor
    )

    List(a, b, c)
  }
}
