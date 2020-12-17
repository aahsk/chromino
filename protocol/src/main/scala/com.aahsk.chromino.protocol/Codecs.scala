package com.aahsk.chromino.protocol

import com.aahsk.chromino.domain.{
  BoardChromino,
  Chromino,
  ChrominoColor,
  ChrominoSquare,
  Position,
  Rotation,
  User
}
import io.circe.generic.semiauto.deriveCodec
import io.circe.{Codec, Decoder, Encoder}
import io.circe.parser._
import io.circe.syntax._
import cats.implicits._
import io.circe

import scala.scalajs.js.annotation._

object Codecs {
  import Message._

  // Entities
  implicit val UserCodec: Codec[User]                     = deriveCodec[User]
  implicit val ChrominoColorCodec: Codec[ChrominoColor]   = deriveCodec[ChrominoColor]
  implicit val ChrominoCodec: Codec[Chromino]             = deriveCodec[Chromino]
  implicit val PositionCodec: Codec[Position]             = deriveCodec[Position]
  implicit val RotationCodec: Codec[Rotation]             = deriveCodec[Rotation]
  implicit val BoardChrominoCodec: Codec[BoardChromino]   = deriveCodec[BoardChromino]
  implicit val ChrominoSquareCodec: Codec[ChrominoSquare] = deriveCodec[ChrominoSquare]
  implicit val BoardStateCodec: Codec[BoardState]         = deriveCodec[BoardState]
  implicit val GameStateCodec: Codec[GameState]           = deriveCodec[GameState]

  // Messages
  implicit val GameStateMessageCodec: Codec[GameStateMessage]     = deriveCodec[GameStateMessage]
  implicit val MessageParseErrorCodec: Codec[MessageParseError]   = deriveCodec[MessageParseError]
  implicit val GameNotFoundErrorCodec: Codec[GameNotFoundError]   = deriveCodec[GameNotFoundError]
  implicit val ConnectionMigratedCodec: Codec[ConnectionMigrated] = deriveCodec[ConnectionMigrated]
  implicit val PlayerJoinedCodec: Codec[PlayerJoined]             = deriveCodec[PlayerJoined]
  implicit val SubmitMoveCodec: Codec[SubmitMove]                 = deriveCodec[SubmitMove]
  implicit val InvalidMoveCodec: Codec[InvalidMoveError]          = deriveCodec[InvalidMoveError]
  implicit val ReceivedOutgoingErrorCodec: Codec[ReceivedOutgoingError] =
    deriveCodec[ReceivedOutgoingError]

  // Message
  implicit val MessageWrapCodec: Codec[MessageWrap] = deriveCodec[MessageWrap]
  implicit val MessageDecoder: Decoder[Message] = Decoder[MessageWrap].emap {
    case MessageWrap("gameStateMessage", payload) =>
      Decoder[GameStateMessage].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("messageParseError", payload) =>
      Decoder[MessageParseError].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("gameNotFoundError", payload) =>
      Decoder[GameNotFoundError].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("connectionMigrated", payload) =>
      Decoder[ConnectionMigrated].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("playerJoined", payload) =>
      Decoder[PlayerJoined].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("submitMove", payload) =>
      Decoder[SubmitMove].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("invalidMoveError", payload) =>
      Decoder[InvalidMoveError].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap("receivedOutgoingError", payload) =>
      Decoder[ReceivedOutgoingError].widen[Message].decodeJson(payload).leftMap(_.message)
    case MessageWrap(_, _) => Left("Unknown command constant")
  }
  implicit val MessageEncoder: Encoder[Message] = Encoder.instance {
    case m: GameStateMessage      => MessageWrap("gameStateMessage", m.asJson).asJson
    case m: MessageParseError     => MessageWrap("messageParseError", m.asJson).asJson
    case m: GameNotFoundError     => MessageWrap("gameNotFoundError", m.asJson).asJson
    case m: ConnectionMigrated    => MessageWrap("connectionMigrated", m.asJson).asJson
    case m: PlayerJoined          => MessageWrap("playerJoined", m.asJson).asJson
    case m: SubmitMove            => MessageWrap("submitMove", m.asJson).asJson
    case m: InvalidMoveError      => MessageWrap("invalidMoveError", m.asJson).asJson
    case m: ReceivedOutgoingError => MessageWrap("receivedOutgoingError", m.asJson).asJson
  }
  implicit val MessageCodec: Codec[Message] = Codec.from(MessageDecoder, MessageEncoder)
}
