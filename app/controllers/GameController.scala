package controllers

import javax.inject._

import play.api.mvc.{AbstractController, ControllerComponents}

class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def startGame() = Action { implicit request =>
    Ok(views.html.start_game())
  }
}
