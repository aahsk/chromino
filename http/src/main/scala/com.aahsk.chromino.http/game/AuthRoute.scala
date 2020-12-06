package com.aahsk.chromino.http.game

import GameSubroute.GameSubroute
import cats.Monad
import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.aahsk.chromino.domain.{AnonymousUser, LocalUser}
import com.aahsk.chromino.logic.Auth.{
  createAnonymousUser,
  validateAnonymousAuth
}
import com.aahsk.chromino.persistance.Database.Database
import com.aahsk.chromino.protocol.Connection
import com.aahsk.chromino.protocol.auth.anon.AnonymousRegister.{
  IncomingAnonymousRegister,
  OutgoingAnonymousRegister,
  protocol => anonRegistrationProtocol
}
import com.aahsk.chromino.protocol.auth.anon.AnonymousLogin.{
  IncomingAnonymousLogin,
  OutgoingAnonymousLogin,
  protocol => anonLoginProtocol
}
import cats.implicits._

case class AuthRoute[F[_]: Sync](
    database: Database[F],
    connectionRef: Ref[F, Connection]
) {
  def anonymousRegisterRoute(): GameSubroute[F] =
    GameSubroute.ofProtocol(anonRegistrationProtocol) {
      case IncomingAnonymousRegister(nick) =>
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

  def anonymousLoginRoute(): GameSubroute[F] =
    GameSubroute.ofProtocol(anonLoginProtocol) {
      case IncomingAnonymousLogin(nick, secret) =>
        for {
          data <- database.get
          user <- Monad[F].pure(data.getUserByNick(nick))
          isValidAuth <- Monad[F].pure(user.exists {
            case u: AnonymousUser => validateAnonymousAuth(u, secret)
            // I would love non-anonymous users with hashed passwords, but that's not implemented currently
            // because I'm going for a barebones MVP product :)
            case u: LocalUser => ???
          })
          _ <- connectionRef.set(if (isValidAuth) {
            Connection(user)
          } else {
            Connection(None)
          })
          response <- Monad[F].pure(
            OutgoingAnonymousLogin(nick, success = isValidAuth)
          )
        } yield response
    }

  def createRoutes(): GameSubroute[F] = {
    anonymousRegisterRoute() <+> anonymousLoginRoute()
  }
}
