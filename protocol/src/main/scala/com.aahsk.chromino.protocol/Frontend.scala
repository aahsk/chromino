package com.aahsk.chromino.protocol

import io.circe.parser._
import io.circe.syntax._
import Codecs._
import com.aahsk.chromino.domain.Rotation
import com.aahsk.chromino.domain.Rotation.{antiClockwise, clockwise}
import io.circe.Json

import scala.scalajs.js.annotation.{JSExport, _}
import scala.scalajs.js._

@JSExportTopLevel("Frontend")
class Frontend(
  jsonParse: Function1[String, Dynamic],
  jsonStringify: Function1[Dynamic, String]
) {
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
  ): Dynamic =
    toJS(json.as[Rotation].map(clockwise(_)).map(_.asJson).getOrElse(Json.Null))

  @JSExport
  def rotateAntiClockwise(
    json: Json
  ): Dynamic =
    toJS(json.as[Rotation].map(antiClockwise(_)).map(_.asJson).getOrElse(Json.Null))
}
