package com.aahsk.chromino.domain

sealed trait Color
object Color {
  final case object Red extends Color
  final case object Blue extends Color
  final case object Purple extends Color
  final case object Yellow extends Color
  final case object Green extends Color
  final case object YinYang extends Color

  val values = List(
    Red,
    Blue,
    Purple,
    Yellow,
    Green,
    YinYang
  )
}

sealed trait Rotation
object Rotation {
  final case object Deg0 extends Rotation
  final case object Deg90 extends Rotation
  final case object Deg180 extends Rotation
  final case object Deg270 extends Rotation

  val values = List(
    Deg0,
    Deg90,
    Deg180,
    Deg270
  )
}

case class Square(x: Int, y: Int, color: Color)

sealed trait Piece {
  def squares(): List[Square]
}

trait Chromino {
  import Rotation._

  val centerX: Int
  val centerY: Int

  val left: Color
  val center: Color
  val right: Color

  val rotation: Rotation

  def squares: List[Square] = {
    val a = Square(
      rotation match {
        case Deg0   => centerX - 1
        case Deg90  => centerX
        case Deg180 => centerX + 1
        case Deg270 => centerX
      },
      rotation match {
        case Deg0   => centerY
        case Deg90  => centerY - 1
        case Deg180 => centerY
        case Deg270 => centerY + 1
      },
      left
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

case class Regular(
    centerX: Int,
    centerY: Int,
    left: Color,
    center: Color,
    right: Color,
    rotation: Rotation
) extends Piece
    with Chromino {}
case class Wildcard(
    centerX: Int,
    centerY: Int,
    left: Color,
    right: Color,
    rotation: Rotation
) extends Piece
    with Chromino {
  val center: Color = Color.YinYang
}
