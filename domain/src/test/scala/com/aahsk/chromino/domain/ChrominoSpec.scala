package com.aahsk.chromino.domain

import org.scalatest.freespec.AnyFreeSpec

class ChrominoSpec extends AnyFreeSpec {
  "chromino should be equal to self if reversed" in {
    import ChrominoColor._
    val a: ChrominoBlueprint = new ChrominoBlueprint {
      val leftColor: ChrominoColor   = R
      val centerColor: ChrominoColor = B
      val rightColor: ChrominoColor  = Y
    }
    val b: ChrominoBlueprint = new ChrominoBlueprint {
      val leftColor: ChrominoColor   = R
      val centerColor: ChrominoColor = B
      val rightColor: ChrominoColor  = Y
    }
    assert(a == b)
  }
}
