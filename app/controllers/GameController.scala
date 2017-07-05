package controllers

import javax.inject._

import connectors.BotConnector
import play.api.mvc._
import forms.StartGameForm.startGameForm
import play.api.Logger
import play.api.i18n.I18nSupport

import scala.concurrent.Future

class GameController @Inject()(cc: ControllerComponents, botConnector: BotConnector) extends AbstractController(cc) with I18nSupport {

  def startGame() = Action { implicit request =>
    val messages = request.messages
    Ok(views.html.start_game(startGameForm))
  }

  def startGameSubmit() = Action.async { implicit request=>
    startGameForm.bindFromRequest().fold(
      formWithErrors => {
        Logger.debug(s"* Bad request to startGamePost with this payload: $formWithErrors")
        Future.successful(BadRequest(views.html.start_game(formWithErrors)))
      }
      , {
        gameInfo =>
          Logger.debug(s"Request to startGamePost with this payload: $gameInfo")
          Future.successful(Redirect(routes.GameController.gameStarted))
      }
    )
  }

  def gameStarted() = Action { implicit request =>
    Ok(views.html.game_started())
  }
}
