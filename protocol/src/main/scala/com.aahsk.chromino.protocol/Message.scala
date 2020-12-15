package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.User
import io.circe.Json

sealed trait Message

object Message {
  final case class GameStateMessage(state: GameState) extends Message
  final case class MessageParseError(error: String)   extends Message
  final case class GameNotFoundError()                extends Message
  final case class Ping()                             extends Message
  final case class Pong()                             extends Message
  final case class ConnectionMigrated()               extends Message
  final case class PlayerJoined(players: List[User])  extends Message
}

case class MessageWrap(command: String, payload: Json)
