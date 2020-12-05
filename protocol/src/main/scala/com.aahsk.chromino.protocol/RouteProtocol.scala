package com.aahsk.chromino.protocol

import io.circe.Decoder.Result

case class RouteProtocol[I, O](
    path: List[String],
    decode: GameRequest => Result[I],
    encode: O => GameResponse
)
