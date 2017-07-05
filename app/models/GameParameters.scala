package models

import play.api.libs.json.{Format, Json}

case class GameParameters (pointsToWin: Int, maxRounds: Int, dynamiteCount: Int)

object GameParameters {
  implicit val formats: Format[GameParameters] = Json.format[GameParameters]
}