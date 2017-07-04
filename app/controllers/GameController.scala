package controllers

import javax.inject._

import play.api.mvc.{AbstractController, ControllerComponents}
import forms.StartGameForm.startGameForm

import scala.concurrent.Future

class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def startGame() = Action { implicit request =>
    Ok(views.html.start_game())
  }

  def startGamePost() = Action.async { implicit request =>
    startGameForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest),
      gameInfo => Future.successful(Ok)
    )
  }
}
