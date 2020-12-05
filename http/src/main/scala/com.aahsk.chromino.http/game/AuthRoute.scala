package com.aahsk.chromino.http.game

import java.util.concurrent.atomic.AtomicReference

import cats.effect.Concurrent
import GameSubroute.GameSubroute
import cats.Applicative
import com.aahsk.chromino.protocol.Connection
import com.aahsk.chromino.protocol.auth.anon.AnonymousRegister.{
  IncomingAnonymousRegister,
  OutgoingAnonymousRegister,
  protocol => anonRegistrationProtocol
}

case class AuthRoute[F[_]: Concurrent](
    connection: AtomicReference[Connection]
) {
  def anonymousRegisterRoute(): GameSubroute[F] =
    GameSubroute.ofProtocol(anonRegistrationProtocol) {
      case (_, IncomingAnonymousRegister(nick)) =>
        Applicative[F].pure(OutgoingAnonymousRegister(nick, success = true))
    }

  def createRoutes(): GameSubroute[F] = {
    anonymousRegisterRoute()
  }
}
