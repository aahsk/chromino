package com.aahsk.chromino.protocol.auth.anon

import com.aahsk.chromino.protocol.{Message, RouteProtocol}
import io.circe.Codec
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax._

object AnonymousRegister {
  val path = List("auth", "anon", "register")

  case class IncomingAnonymousRegister(nick: String)
  implicit val incomingCodec: Codec[IncomingAnonymousRegister] =
    deriveCodec[IncomingAnonymousRegister]
  def ofIncoming(message: Message): Result[IncomingAnonymousRegister] =
    message.getData().as[IncomingAnonymousRegister]

  case class OutgoingAnonymousRegister(
      nick: String,
      secret: String,
      success: Boolean
  )
  implicit val outgoingCodec: Codec[OutgoingAnonymousRegister] =
    deriveCodec[OutgoingAnonymousRegister]
  def toOutgoing(response: OutgoingAnonymousRegister): Message =
    Message(path, Some(response.asJson))

  val protocol =
    RouteProtocol[IncomingAnonymousRegister, OutgoingAnonymousRegister](
      path,
      ofIncoming,
      toOutgoing
    )
}

// { "path": [ "auth", "anon", "register" ], "data": { "nick": "anon1" }}
// { "path": [ "auth", "anon", "login" ], "data": { "nick": "anon1", "secret": "77daf8e9-0ee5-48fd-9ce4-2e30bd1e3cdf" }}
