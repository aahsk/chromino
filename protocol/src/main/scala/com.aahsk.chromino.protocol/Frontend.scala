package com.aahsk.chromino.protocol

import io.circe.parser._
import io.circe.syntax._
import io.circe.scalajs._

import scala.scalajs.js.annotation._
import scala.scalajs.js
import Codecs._
import io.circe.Json

@JSExportTopLevel("Frontend")
object Frontend {
  @JSExport
  def isValid(
    text: String
  ): Boolean =
    decode[Message](text).isRight

//  @JSExport
//  def boardPieceSquares(
//    text: Json
//  ): Json =
//    decode[Message](text).isRight
}
