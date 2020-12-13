package com.aahsk.chromino.domain

import enumeratum._
import scala.scalajs.js.annotation._

sealed trait ChrominoColor extends EnumEntry

@JSExportTopLevel("ChrominoColor")
case object ChrominoColor extends Enum[ChrominoColor] with CirceEnum[ChrominoColor] {
  final case object R extends ChrominoColor
  final case object B extends ChrominoColor
  final case object P extends ChrominoColor
  final case object Y extends ChrominoColor
  final case object G extends ChrominoColor
  // This is the YinYang color
  final case object X extends ChrominoColor

  val values = findValues
}
