package com.aahsk.chromino.domain

import org.scalatest.freespec.AnyFreeSpec

class ChrominoBagSpec extends AnyFreeSpec {
  // Source - https://boardgamegeek.com/image/708574/chromino
  "bag should contain 75 regular & 5 wildcard pieces" in {
    assert(Chromino.values.count(_.centerColor == ChrominoColor.X) == 75)
    assert(Chromino.values.count(_.centerColor != ChrominoColor.X) == 5)
  }
}
