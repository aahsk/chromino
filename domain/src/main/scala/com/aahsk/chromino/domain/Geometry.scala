package com.aahsk.chromino.domain

import scala.scalajs.js.annotation._

@JSExportTopLevel("Position")
case class Position(x: Int, y: Int)

sealed trait Rotation

@JSExportTopLevel("Rotation")
object Rotation {
  // Encoded as points of compass
  // - [N] North is "default" and means the chromino looks like [ Red, Blue, Purple, ]
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

  val values = List(
    N,
    W,
    S,
    E
  )
}
