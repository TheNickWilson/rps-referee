package controllers

import javax.inject._

import play.api.mvc._
import forms.StartGameForm.startGameForm
import play.api.Logger

import scala.concurrent.Future

class GameController @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) {

  def startGame() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.start_game(startGameForm))
  }

  def startGamePost() = Action.async { implicit request: MessagesRequest[AnyContent] =>
    startGameForm.bindFromRequest().fold(
      formWithErrors => {
        Logger.debug(s"* Bad request to startGamePost with this payload: $formWithErrors")
        Future.successful(BadRequest(views.html.start_game(formWithErrors)))}, {
          gameInfo =>
            Logger.debug(s"Request to startGamePost with this payload: $gameInfo")
            Future.successful(Redirect(routes.GameController.gameStarted))
      }
    )
  }

  def gameStarted() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.game_started())
  }
}
