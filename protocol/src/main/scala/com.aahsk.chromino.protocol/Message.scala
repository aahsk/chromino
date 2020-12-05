package com.aahsk.chromino.protocol

import io.circe.{Codec, Json}
import io.circe.generic.semiauto.deriveCodec

case class Message(path: List[String], data: Option[Json]) {
  def getData(): Json = data.getOrElse(Json.Null)
}
object Message {
  implicit val messageCodec: Codec[Message] = deriveCodec[Message]
}
