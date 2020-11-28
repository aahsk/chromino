package com.aahsk.chromino.domain

case class Position(x: Int, y: Int)

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
