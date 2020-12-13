package com.aahsk.chromino.domain

/** This code is for automatically generating all 70 regular chromino case objects.
  * We want 70 chrominos to be static in memory.
  * We don't want to generate new 70 new objects for every game.
  * I also can't be bothered to write them out by hand.
  * This is the solution - auto-generate them.
  *
  * > sbt
  * > project domain
  * > console
  * > com.aahsk.chromino.domain.ChrominoMeta.printRegulars()
  *
  * Note this auto-generate doesn't strictly guarantee which order for a chromino will be chosen.
  * Meaning RGY is the same chromino as YGR, but which will be generated? Not defined!
  */
object ChrominoMeta {
  def generateRegulars(): Set[ChrominoBlueprint] = {
    val regulars = ChrominoColor.values.filterNot(_ == ChrominoColor.X)
    regulars
      .map(x => List(x))
      .flatMap(xs => regulars.map(x => x :: xs))
      .flatMap(xs => regulars.map(x => x :: xs))
      .map {
        case a :: b :: c :: Nil =>
          Some(new ChrominoBlueprint { val leftColor = a; val centerColor = b; val rightColor = c })
        case _ => None
      }
      .filter(_.isDefined)
      .map(_.get)
      .toSet
  }

  def serializeRegulars(firstIndex: Int = 5): List[String] = {
    def color(c: ChrominoColor) = c match {
      case ChrominoColor.R => "R"
      case ChrominoColor.B => "B"
      case ChrominoColor.P => "P"
      case ChrominoColor.Y => "Y"
      case ChrominoColor.G => "G"
      case ChrominoColor.X => "X"
    }

    generateRegulars().toList.zipWithIndex
      .map { case (c: ChrominoBlueprint, i: Int) =>
        f"case object Regular${color(c.leftColor)}${color(c.centerColor)}${color(c.rightColor)} " +
          f"extends Chromino(${firstIndex + i}, ${color(c.leftColor)}, ${color(c.centerColor)}, ${color(c.rightColor)})"
      }
  }

  def printRegulars(): Unit =
    serializeRegulars().foreach(println)
}
