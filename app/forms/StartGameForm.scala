package forms

import models.{Bot, GameInfo}
import play.api.data.Form
import play.api.data.Forms._

object StartGameForm {
  val startGameForm = Form(
    mapping(
      "pointsToWin" -> number,
      "maxRounds" -> number,
      "dynamiteCount" -> number,
      "bot1" -> mapping(
        "name" -> nonEmptyText,
        "url" -> nonEmptyText
      )(Bot.apply)(Bot.unapply),
      "bot2" -> mapping(
        "name" -> nonEmptyText,
        "url" -> nonEmptyText
      )(Bot.apply)(Bot.unapply)
    )(GameInfo.apply)(GameInfo.unapply)
  )
}
