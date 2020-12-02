package com.aahsk.chromino.http

import cats.effect.Concurrent
import cats.implicits._
import org.http4s.Response
import org.http4s.websocket.WebSocketFrame
import fs2.concurrent.Queue
import org.http4s.server.websocket.WebSocketBuilder
import fs2.Pipe

object WebSocket {
  def unboundedWebsocketPipe[F[_]: Concurrent](
      pipe: Pipe[F, WebSocketFrame, WebSocketFrame]
  ): F[Response[F]] =
    for {
      // Unbounded queue which can OOM only when messages aren't processed quickly enough
      queue <- Queue.unbounded[F, WebSocketFrame]
      response <- WebSocketBuilder[F].build(
        // Sink, where the incoming WebSocket messages from the client are pushed to.
        receive = queue.enqueue,
        // Outgoing stream of WebSocket messages to send to the client.
        send = queue.dequeue.through(pipe)
      )
    } yield response
}
