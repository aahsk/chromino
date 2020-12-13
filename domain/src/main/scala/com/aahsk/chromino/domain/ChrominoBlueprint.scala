package com.aahsk.chromino.domain

trait ChrominoBlueprint extends Equals {
  val leftColor: ChrominoColor
  val centerColor: ChrominoColor
  val rightColor: ChrominoColor

  override def canEqual(that: Any): Boolean =
    that match {
      case chromino: ChrominoBlueprint => true
      case _                           => false
    }

  override def equals(that: Any): Boolean =
    that match {
      case chromino: ChrominoBlueprint =>
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
