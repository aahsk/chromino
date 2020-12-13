package com.aahsk.chromino.domain

import enumeratum.values._

import scala.scalajs.js.annotation._

sealed abstract class Chromino(
  val value: Int,
  val leftColor: ChrominoColor,
  val centerColor: ChrominoColor,
  val rightColor: ChrominoColor
) extends IntEnumEntry
  with ChrominoBlueprint

@JSExportTopLevel("Chromino")
case object Chromino extends IntEnum[Chromino] with IntCirceEnum[Chromino] {
  import ChrominoColor._

  // Wildcard chrominos
  case object WildcardYXP extends Chromino(0, Y, X, P)
  case object WildcardBXY extends Chromino(1, B, X, Y)
  case object WildcardRXP extends Chromino(2, R, X, P)
  case object WildcardGXR extends Chromino(3, G, X, R)
  case object WildcardBXG extends Chromino(4, B, X, G)

  // See ChrominoMeta for information on how to auto-generate regular chrominos
  case object RegularRRR extends Chromino(5, R, R, R)
  case object RegularRRB extends Chromino(6, R, R, B)
  case object RegularRRP extends Chromino(7, R, R, P)
  case object RegularRRY extends Chromino(8, R, R, Y)
  case object RegularRRG extends Chromino(9, R, R, G)
  case object RegularRBR extends Chromino(10, R, B, R)
  case object RegularRBB extends Chromino(11, R, B, B)
  case object RegularRBP extends Chromino(12, R, B, P)
  case object RegularRBY extends Chromino(13, R, B, Y)
  case object RegularRBG extends Chromino(14, R, B, G)
  case object RegularRPR extends Chromino(15, R, P, R)
  case object RegularRPB extends Chromino(16, R, P, B)
  case object RegularRPP extends Chromino(17, R, P, P)
  case object RegularRPY extends Chromino(18, R, P, Y)
  case object RegularRPG extends Chromino(19, R, P, G)
  case object RegularRYR extends Chromino(20, R, Y, R)
  case object RegularRYB extends Chromino(21, R, Y, B)
  case object RegularRYP extends Chromino(22, R, Y, P)
  case object RegularRYY extends Chromino(23, R, Y, Y)
  case object RegularRYG extends Chromino(24, R, Y, G)
  case object RegularRGR extends Chromino(25, R, G, R)
  case object RegularRGB extends Chromino(26, R, G, B)
  case object RegularRGP extends Chromino(27, R, G, P)
  case object RegularRGY extends Chromino(28, R, G, Y)
  case object RegularRGG extends Chromino(29, R, G, G)
  case object RegularBRB extends Chromino(30, B, R, B)
  case object RegularBRP extends Chromino(31, B, R, P)
  case object RegularBRY extends Chromino(32, B, R, Y)
  case object RegularBRG extends Chromino(33, B, R, G)
  case object RegularBBB extends Chromino(34, B, B, B)
  case object RegularBBP extends Chromino(35, B, B, P)
  case object RegularBBY extends Chromino(36, B, B, Y)
  case object RegularBBG extends Chromino(37, B, B, G)
  case object RegularBPB extends Chromino(38, B, P, B)
  case object RegularBPP extends Chromino(39, B, P, P)
  case object RegularBPY extends Chromino(40, B, P, Y)
  case object RegularBPG extends Chromino(41, B, P, G)
  case object RegularBYB extends Chromino(42, B, Y, B)
  case object RegularBYP extends Chromino(43, B, Y, P)
  case object RegularBYY extends Chromino(44, B, Y, Y)
  case object RegularBYG extends Chromino(45, B, Y, G)
  case object RegularBGB extends Chromino(46, B, G, B)
  case object RegularBGP extends Chromino(47, B, G, P)
  case object RegularBGY extends Chromino(48, B, G, Y)
  case object RegularBGG extends Chromino(49, B, G, G)
  case object RegularPRP extends Chromino(50, P, R, P)
  case object RegularPRY extends Chromino(51, P, R, Y)
  case object RegularPRG extends Chromino(52, P, R, G)
  case object RegularPBP extends Chromino(53, P, B, P)
  case object RegularPBY extends Chromino(54, P, B, Y)
  case object RegularPBG extends Chromino(55, P, B, G)
  case object RegularPPP extends Chromino(56, P, P, P)
  case object RegularPPY extends Chromino(57, P, P, Y)
  case object RegularPPG extends Chromino(58, P, P, G)
  case object RegularPYP extends Chromino(59, P, Y, P)
  case object RegularPYY extends Chromino(60, P, Y, Y)
  case object RegularPYG extends Chromino(61, P, Y, G)
  case object RegularPGP extends Chromino(62, P, G, P)
  case object RegularPGY extends Chromino(63, P, G, Y)
  case object RegularPGG extends Chromino(64, P, G, G)
  case object RegularYRY extends Chromino(65, Y, R, Y)
  case object RegularYRG extends Chromino(66, Y, R, G)
  case object RegularYBY extends Chromino(67, Y, B, Y)
  case object RegularYBG extends Chromino(68, Y, B, G)
  case object RegularYPY extends Chromino(69, Y, P, Y)
  case object RegularYPG extends Chromino(70, Y, P, G)
  case object RegularYYY extends Chromino(71, Y, Y, Y)
  case object RegularYYG extends Chromino(72, Y, Y, G)
  case object RegularYGY extends Chromino(73, Y, G, Y)
  case object RegularYGG extends Chromino(74, Y, G, G)
  case object RegularGRG extends Chromino(75, G, R, G)
  case object RegularGBG extends Chromino(76, G, B, G)
  case object RegularGPG extends Chromino(77, G, P, G)
  case object RegularGYG extends Chromino(78, G, Y, G)
  case object RegularGGG extends Chromino(79, G, G, G)

  val values = findValues
}
