package com.aahsk.chromino.protocol

import java.util.concurrent.atomic.AtomicReference

case class GameRequest(
    message: Message,
    connection: AtomicReference[Connection]
)
