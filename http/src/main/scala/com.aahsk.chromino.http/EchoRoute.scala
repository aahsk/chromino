package com.aahsk.chromino.http

import cats.effect.Concurrent
import fs2.Pipe
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.websocket.WebSocketFrame

object EchoRoute {
  def pipe[F[_]](): Pipe[F, WebSocketFrame, WebSocketFrame] =
    _.collect {
      case WebSocketFrame.Text(message, _) => WebSocketFrame.Text(message)
    }

  private def chrominoRoute[F[_]: Concurrent](): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "echo" =>
        WebSocket.unboundedWebsocketPipe(pipe[F]())
    }

  def routes[F[_]: Concurrent](): HttpRoutes[F] =
    chrominoRoute[F]()
}
