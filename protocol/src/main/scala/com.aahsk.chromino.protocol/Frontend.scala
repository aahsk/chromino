package com.aahsk.chromino.protocol

import io.circe.parser._
import io.circe.syntax._
import Codecs._
import com.aahsk.chromino.domain.{BoardChromino, ChrominoColor, Rotation}
import io.circe.Json

import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.js._

@JSExportTopLevel("Frontend")
class Frontend(
  jsonParse: Function1[String, Dynamic],
  jsonStringify: Function1[Dynamic, String]
) {
  // Scala <-> TypeScript
  def toJS(json: Json): Dynamic = {
    jsonParse(json.spaces2)
  }

  @JSExport
  def toScala(dynamic: Dynamic): Json = {
    parse(jsonStringify(dynamic)).getOrElse(Json.Null)
  }

  @JSExport
  def parseMessage(
    text: String
  ): Dynamic =
    toJS(decode[Message](text).toOption.asJson)

  // Rotation
  @JSExport
  val N = toJS(Rotation.N.asInstanceOf[Rotation].asJson)

  @JSExport
  val E = toJS(Rotation.N.asInstanceOf[Rotation].asJson)

  @JSExport
  val S = toJS(Rotation.N.asInstanceOf[Rotation].asJson)

  @JSExport
  val W = toJS(Rotation.N.asInstanceOf[Rotation].asJson)

  @JSExport
  def rotateClockwise(
    json: Json
  ): Dynamic = {
    toJS(json.as[Rotation].map(_.clockwise()).map(_.asJson).getOrElse(Json.Null))
  }

  @JSExport
  def rotateAntiClockwise(
    json: Json
  ): Dynamic =
    toJS(json.as[Rotation].map(_.antiClockwise()).map(_.asJson).getOrElse(Json.Null))

  // Chromino color
  @JSExport
  val R = toJS(ChrominoColor.R.asInstanceOf[ChrominoColor].asJson)

  @JSExport
  val B = toJS(ChrominoColor.B.asInstanceOf[ChrominoColor].asJson)

  @JSExport
  val P = toJS(ChrominoColor.P.asInstanceOf[ChrominoColor].asJson)

  @JSExport
  val Y = toJS(ChrominoColor.Y.asInstanceOf[ChrominoColor].asJson)

  @JSExport
  val G = toJS(ChrominoColor.G.asInstanceOf[ChrominoColor].asJson)

  @JSExport
  val X = toJS(ChrominoColor.X.asInstanceOf[ChrominoColor].asJson)

  // Chromino
  @JSExport
  def toSquares(
    json: Json
  ): Dynamic =
    toJS(json.as[BoardChromino].map(_.squared()).map(_.asJson).getOrElse(Json.Null))
}
