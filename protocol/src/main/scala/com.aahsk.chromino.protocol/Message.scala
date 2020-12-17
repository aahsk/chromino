package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{BoardChromino, User}
import io.circe.Json

sealed trait Message
sealed trait Outgoing

object Message {
  final case class GameStateMessage(state: GameState)       extends Message with Outgoing
  final case class MessageParseError(error: String)         extends Message with Outgoing
  final case class GameNotFoundError()                      extends Message with Outgoing
  final case class ConnectionMigrated()                     extends Message with Outgoing
  final case class PlayerJoined(players: List[User])        extends Message with Outgoing
  final case class SubmitMove(boardChromino: BoardChromino) extends Message
  final case class InvalidMoveError(error: String)          extends Message with Outgoing
  final case class ReceivedOutgoingError()                  extends Message
}

case class MessageWrap(command: String, payload: Json)
