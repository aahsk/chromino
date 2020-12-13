package com.aahsk.chromino.protocol

import io.circe.Json
import scala.scalajs.js.annotation.JSExportTopLevel

sealed trait Message

@JSExportTopLevel("Message")
object Message {
  final case class GameStateMessage(state: GameState) extends Message
  final case class MessageParseError(error: String)   extends Message
  final case class GameNotFoundError()                extends Message
  final case class Ping()                             extends Message
  final case class Pong()                             extends Message
}

@JSExportTopLevel("MessageWrap")
case class MessageWrap(command: String, payload: Json)
