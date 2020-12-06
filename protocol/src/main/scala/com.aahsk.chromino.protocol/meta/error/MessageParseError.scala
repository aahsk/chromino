package com.aahsk.chromino.protocol.meta.error

import com.aahsk.chromino.protocol.Message
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax._

object MessageParseError {
  val path = List("meta", "error", "messageParseError")

  case class OutgoingMessageParseError(error: String)
  implicit val outgoingCodec: Codec[OutgoingMessageParseError] =
    deriveCodec[OutgoingMessageParseError]
  def toOutgoing(error: OutgoingMessageParseError): Message =
    Message(path, Some(error.asJson))
}
