package com.aahsk.chromino.protocol.auth.anon

import com.aahsk.chromino.protocol.{Message, RouteProtocol}
import io.circe.Codec
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax._

object AnonymousLogin {
  val path = List("auth", "anon", "login")

  case class IncomingAnonymousLogin(nick: String, secret: String)
  implicit val incomingCodec: Codec[IncomingAnonymousLogin] =
    deriveCodec[IncomingAnonymousLogin]
  def ofIncoming(message: Message): Result[IncomingAnonymousLogin] =
    message.getData().as[IncomingAnonymousLogin]

  case class OutgoingAnonymousLogin(
      nick: String,
      success: Boolean
  )
  implicit val outgoingCodec: Codec[OutgoingAnonymousLogin] =
    deriveCodec[OutgoingAnonymousLogin]
  def toOutgoing(response: OutgoingAnonymousLogin): Message =
    Message(path, Some(response.asJson))

  val protocol =
    RouteProtocol[IncomingAnonymousLogin, OutgoingAnonymousLogin](
      path,
      ofIncoming,
      toOutgoing
    )
}
