package com.aahsk.chromino.http.game

import java.util.concurrent.atomic.AtomicReference

import GameSubroute.GameSubroute
import cats.Monad
import com.aahsk.chromino.domain.{AnonymousUser, User}
import com.aahsk.chromino.logic.Auth.createAnonymousUser
import com.aahsk.chromino.persistance.Database.Database
import com.aahsk.chromino.protocol.Connection
import com.aahsk.chromino.protocol.auth.anon.AnonymousRegister.{
  IncomingAnonymousRegister,
  OutgoingAnonymousRegister,
  protocol => anonRegistrationProtocol
}
import cats.implicits._

case class AuthRoute[F[_]: Monad](
    database: Database[F],
    connection: AtomicReference[Connection]
) {
  def anonymousRegisterRoute(): GameSubroute[F] =
    GameSubroute.ofProtocol(anonRegistrationProtocol) {
      case (_, IncomingAnonymousRegister(nick)) =>
        for {
          user <-
            database
              .modify(_.insertUser(createAnonymousUser(nick)))
              .asInstanceOf[F[AnonymousUser]] // :(
          response <- Monad[F].pure(
            OutgoingAnonymousRegister(nick, user.secret, success = true)
          )
        } yield response
    }

  def createRoutes(): GameSubroute[F] = {
    anonymousRegisterRoute()
  }
}
