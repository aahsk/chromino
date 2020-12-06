package com.aahsk.chromino.protocol

import io.circe.Decoder.Result

case class RouteProtocol[I, O](
    path: List[String],
    decode: Message => Result[I],
    encode: O => Message
)
