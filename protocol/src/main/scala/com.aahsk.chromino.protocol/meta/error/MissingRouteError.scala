package com.aahsk.chromino.protocol.meta.error

import com.aahsk.chromino.protocol.{GameResponse, Message}

object MissingRouteError {
  val path = List("meta", "error", "missingRouteError")

  def toOutgoing(): GameResponse =
    GameResponse(Message(path, None))
}
