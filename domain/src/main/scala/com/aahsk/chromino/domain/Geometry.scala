package com.aahsk.chromino.domain

import enumeratum.{CirceEnum, Enum, EnumEntry}
import scala.scalajs.js.annotation._
import enumeratum._

@JSExportTopLevel("Position")
case class Position(x: Int, y: Int)
//  Position is imagined with the following axis
//
//          ^ y
//          |
//          |
//  ----------------> x
//          |
//          |
//          |

sealed trait Rotation extends EnumEntry {
  import Rotation._

  def clockwise(): Rotation = this match {
    case N => E
    case W => N
    case S => W
    case E => S
  }

  def antiClockwise(): Rotation = this match {
    case N => W
    case W => S
    case S => E
    case E => N
  }
}

@JSExportTopLevel("Rotation")
case object Rotation extends Enum[Rotation] with CirceEnum[Rotation] {
  // Encoded as points of compass
  // - [N] North is "default" and means an RBP chromino looks like [ Red, Blue, Purple, ]
  // - [W] West means the chromino looks like
  //    [
  //    Purple,
  //    Blue,
  //    Red,
  //    ]
  // - [S] South means the chromino looks like
  //    [ Purple, Blue, Red, ]
  // - [E] East means the chromino looks like
  //    [
  //    Red,
  //    Blue,
  //    Purple,
  //    ]
  final case object N extends Rotation
  final case object W extends Rotation
  final case object S extends Rotation
  final case object E extends Rotation

  val values = findValues
}
