package com.aahsk.chromino.protocol

import org.scalatest.freespec.AnyFreeSpec
import io.circe.parser._
import com.aahsk.chromino.protocol.Codecs._
import com.aahsk.chromino.protocol.Message._
import io.circe.{Json, JsonObject}
import cats.implicits._

class CodecsSpec extends AnyFreeSpec {
  "message wrap should be decodable" in {
    val ping = "{ \"command\": \"commandName\", \"payload\": {} }"
    decode[MessageWrap](ping) == Right(
      MessageWrap("commandName", Json.fromJsonObject(JsonObject.empty))
    )
  }

  "invalid move message should be decodable" in {
    val ping = "{ \"error\": \"Bad move\" }"
    assert(decode[InvalidMoveError](ping) == Right(InvalidMoveError("Bad move")))
  }

  "wrapped invalid move message should be decodable" in {
    val ping = "{ \"command\": \"invalidMoveError\", \"payload\": { \"error\": \"Bad move\" } }"

    assert(decode[Message](ping).leftMap(_.getMessage()) == Right(InvalidMoveError("Bad move")))
  }
}
