package com.aahsk.chromino.domain

import org.scalatest.flatspec.AnyFlatSpec

class ChrominoBagSpec extends AnyFlatSpec {
  // Source - https://boardgamegeek.com/image/708574/chromino
  "bag" should "contain 75 + 5 pieces" in {
    val pieces = ChrominoBag.ofAll().pieces

    val regularCount = pieces.count {
      case _: Regular  => true
      case _: Wildcard => false
    }

    val wildcardCount = pieces.count {
      case _: Regular  => false
      case _: Wildcard => true
    }

    assert(regularCount == 75)
    assert(wildcardCount == 5)
  }
}
