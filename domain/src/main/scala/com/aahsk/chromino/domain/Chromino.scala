package com.aahsk.chromino.domain

sealed trait Color

object Color {
  final case object Red extends Color
  final case object Blue extends Color
  final case object Purple extends Color
  final case object Yellow extends Color
  final case object Green extends Color
  final case object YinYang extends Color

  val regulars: List[Color] = List(
    Red,
    Blue,
    Purple,
    Yellow,
    Green
  )

  val values: List[Color] = regulars :+ YinYang
}

sealed trait Chromino extends Equals {
  val leftColor: Color
  val centerColor: Color
  val rightColor: Color

  override def equals(that: Any): Boolean =
    that match {
      case chromino: Chromino =>
        val centerEqual = centerColor == chromino.centerColor
        val exactEqual =
          leftColor == chromino.leftColor && rightColor == chromino.rightColor
        val reverseEqual =
          leftColor == chromino.rightColor && rightColor == chromino.leftColor
        centerEqual && (exactEqual || reverseEqual)
      case _ => true
    }

  // This reduces performance and shouldn't be done in production
  // By default case class equality is first checked by matching hashCodes
  // If hashCodes match, then more precise equals() is triggered
  // The default hashCode is incorrect for this class and I don't want to implement it right now
  // So I'm just setting it to 0 so that it works at least :P
  override def hashCode: Int = 0
}

case class Regular(
    leftColor: Color,
    centerColor: Color,
    rightColor: Color
) extends Chromino {}

case class Wildcard(
    leftColor: Color,
    rightColor: Color
) extends Chromino {
  val centerColor: Color = Color.YinYang
}

object Chromino {
  def regulars(): Set[Regular] =
    Color.regulars
      .map(x => List(x))
      .flatMap(xs => Color.regulars.map(x => x :: xs))
      .flatMap(xs => Color.regulars.map(x => x :: xs))
      .map {
        case a :: b :: c :: Nil => Some(Regular(a, b, c))
        case _                  => None
      }
      .filter(_.isDefined)
      .map(_.get)
      .toSet

  def wildcards(): Set[Wildcard] =
    Set(
      Wildcard(Color.Yellow, Color.Purple),
      Wildcard(Color.Blue, Color.Yellow),
      Wildcard(Color.Red, Color.Purple),
      Wildcard(Color.Green, Color.Red),
      Wildcard(Color.Blue, Color.Green)
    )

  def all(): Set[Chromino] = Set[Chromino]() ++ regulars() ++ wildcards()
}
