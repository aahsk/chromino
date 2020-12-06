package com.aahsk.chromino.http.game

import cats.{Applicative, Monad}
import cats.data.{Kleisli, OptionT}
import com.aahsk.chromino.protocol.{Connection, Message, RouteProtocol}
import cats.implicits._

/**
  * This functionality is _heavily_ inspired from http4s' HttpRoute source code :D
  */
object GameSubroute {
  type GameSubroute[F[_]] =
    Kleisli[OptionT[F, *], Message, Message]

  def of[F[_]: Applicative](
      pf: PartialFunction[Message, F[Message]]
  ): GameSubroute[F] =
    Kleisli(req => OptionT(pf.lift(req).sequence))

  def ofProtocol[F[_]: Monad, I, O](
      protocol: RouteProtocol[I, O]
  )(
      execute: I => F[O]
  ): GameSubroute[F] = {
    Kleisli(message => {
      if (message.path != protocol.path) {
        OptionT.none
      } else {
        OptionT(
          protocol
            .decode(message)
            .toOption
            .traverse(input => execute(input))
        ).map(protocol.encode)
      }
    })
  }
}
