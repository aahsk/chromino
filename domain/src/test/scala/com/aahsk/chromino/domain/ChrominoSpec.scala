package com.aahsk.chromino.domain

import org.scalatest.freespec.AnyFreeSpec

class ChrominoSpec extends AnyFreeSpec {
  "chromino should be equal to self if reversed" in {
    assert(Chromino.RegularRBY == Chromino.RegularRBY)
  }
}
