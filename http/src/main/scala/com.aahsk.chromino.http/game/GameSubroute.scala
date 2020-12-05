package com.aahsk.chromino.http.game

import cats.{Applicative, Monad}
import cats.data.{Kleisli, OptionT}
import com.aahsk.chromino.protocol.{GameRequest, GameResponse, RouteProtocol}
import cats.implicits._

/**
  * This functionality is _heavily_ inspired from http4s' HttpRoute source code :D
  */
object GameSubroute {
  type GameSubroute[F[_]] =
    Kleisli[OptionT[F, *], GameRequest, GameResponse]

  def of[F[_]: Applicative](
      pf: PartialFunction[GameRequest, F[GameResponse]]
  ): GameSubroute[F] =
    Kleisli(req => OptionT(pf.lift(req).sequence))

  def ofProtocol[F[_]: Monad, I, O](
      protocol: RouteProtocol[I, O]
  )(
      execute: (GameRequest, I) => F[O]
  ): GameSubroute[F] = {
    Kleisli(request => {
      if (request.message.path != protocol.path) {
        OptionT.none
      } else {
        OptionT(
          protocol
            .decode(request)
            .toOption
            .traverse(input => execute(request, input))
        ).map(protocol.encode)
      }
    })
  }
}
