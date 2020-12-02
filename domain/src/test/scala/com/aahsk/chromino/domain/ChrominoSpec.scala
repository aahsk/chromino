package com.aahsk.chromino.domain

import com.aahsk.chromino.domain.Color._
import org.scalatest.flatspec.AnyFlatSpec

class ChrominoSpec extends AnyFlatSpec {
  "chromino" should "be equal to self if reversed" in {
    assert(Regular(Red, Blue, Yellow) == Regular(Yellow, Blue, Red))
  }
}
