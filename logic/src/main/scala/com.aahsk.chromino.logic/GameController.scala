package com.aahsk.chromino.logic

import cats.Monad
import fs2.concurrent.Queue
import com.aahsk.chromino.domain.Game
import com.aahsk.chromino.protocol.Message
import com.aahsk.chromino.protocol.Message.Pong

class GameController[F[_]: Monad](
  var game: Game,
  var toPlayers: Map[String, Queue[F, Message]]
) {
  def process(nick: String, message: Message): F[Message] = Monad[F].pure(Pong())
}
