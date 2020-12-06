package com.aahsk.chromino.protocol.meta.error

import com.aahsk.chromino.protocol.Message

object MissingRouteError {
  val path = List("meta", "error", "missingRouteError")

  def toOutgoing(): Message =
    Message(path, None)
}
