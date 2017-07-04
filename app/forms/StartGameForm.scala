package forms

import models.GameInfo
import play.api.data.Form
import play.api.data.Forms._

object StartGameForm {
  val startGameForm = Form(
    mapping(
      "pointsToWin" -> number,
      "maxRounds" -> number,
      "dynamiteCount" -> number
    )(GameInfo.apply)(GameInfo.unapply)
  )
}
