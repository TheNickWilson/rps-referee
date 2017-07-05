package forms

import models.{BotInfo, GameInfo, GameParameters}
import play.api.data.Form
import play.api.data.Forms._

object StartGameForm {
  val startGameForm = Form(
    mapping(
      "gameParameters" -> mapping("pointsToWin" -> number,
        "maxRounds" -> number,
        "dynamiteCount" -> number)
      (GameParameters.apply)(GameParameters.unapply),
      "bot1" -> mapping(
        "name" -> nonEmptyText,
        "url" -> nonEmptyText
      )(BotInfo.apply)(BotInfo.unapply),
      "bot2" -> mapping(
        "name" -> nonEmptyText,
        "url" -> nonEmptyText
      )(BotInfo.apply)(BotInfo.unapply)
    )(GameInfo.apply)(GameInfo.unapply)
  )
}
