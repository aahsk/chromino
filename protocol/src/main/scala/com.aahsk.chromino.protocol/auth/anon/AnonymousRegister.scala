package com.aahsk.chromino.protocol.auth.anon

import com.aahsk.chromino.protocol.{
  GameRequest,
  GameResponse,
  Message,
  RouteProtocol
}
import io.circe.Codec
import io.circe.Decoder.Result
import io.circe.generic.semiauto.deriveCodec
import io.circe.syntax._

object AnonymousRegister {
  val path = List("auth", "anon", "register")

  case class IncomingAnonymousRegister(nick: String)
  implicit val incomingCodec: Codec[IncomingAnonymousRegister] =
    deriveCodec[IncomingAnonymousRegister]
  def ofIncoming(request: GameRequest): Result[IncomingAnonymousRegister] =
    request.message.getData().as[IncomingAnonymousRegister]

  case class OutgoingAnonymousRegister(nick: String, success: Boolean)
  implicit val outgoingCodec: Codec[OutgoingAnonymousRegister] =
    deriveCodec[OutgoingAnonymousRegister]
  def toOutgoing(response: OutgoingAnonymousRegister): GameResponse =
    GameResponse(Message(path, Some(response.asJson)))

  val protocol =
    RouteProtocol[IncomingAnonymousRegister, OutgoingAnonymousRegister](
      path,
      ofIncoming,
      toOutgoing
    )
}
