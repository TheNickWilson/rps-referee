package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.GET
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

class GameControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "GameContoller GET to /start-game" should {

    "render the page" in {
      val request = FakeRequest(GET, "/start-game")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Start a new game")
    }

    "include a form that POSTs to start-game" in {
      val request = FakeRequest(GET, "/start-game")
      val result = route(app, request).get

      contentAsString(result) must include("""<form action="/start-game" method="POST" >""")
    }
  }

  "GameController startGame" should {
    "respond with 400 to no payload" in {
      val controller = app.injector.instanceOf[GameController]
      val request = FakeRequest().withCSRFToken
      val result = controller.startGamePost().apply(request)

      status(result) mustBe BAD_REQUEST
    }

    "respond to a valid payload by redirecting to the 'game started' page" in {
      val gameInfo = Map(
        "pointsToWin" -> "1000",
        "maxRounds" -> "2000",
        "dynamiteCount" -> "100",
        "bot1.name" -> "Bot 1",
        "bot1.url" -> "http://bot1",
        "bot2.name" -> "Bot 2",
        "bot2.url" -> "http://bot2")
      val request = FakeRequest(POST, "/start-game").withJsonBody(Json.toJson(gameInfo))
      val result = route(app, request).get

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.GameController.gameStarted().url)
    }

    "respond with 400 and an error message to an invalid payload" in {
      val gameInfo = Map("pointsToWin" -> "1000", "maxRounds" -> "2000", "dynamiteCount" -> "abc")
      val controller = app.injector.instanceOf[GameController]
      val request = FakeRequest().withJsonBody(Json.toJson(gameInfo)).withCSRFToken
      val result = controller.startGamePost().apply(request)

      status(result) mustBe BAD_REQUEST
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Numeric value expected")
    }
  }

  "GameController GET to /game-started" should {
    "render the page" in {
      val request = FakeRequest(GET, "/game-started")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Game started!")
    }
  }
}
