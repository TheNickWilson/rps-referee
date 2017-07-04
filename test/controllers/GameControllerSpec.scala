package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.Helpers.GET
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._

class GameControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "GameContoller GET" should {

    "render the page from the router" in {
      val request = FakeRequest(GET, "/start-game")
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include("Start a new game")
    }

    "respond with 400 to a POST with no payload" in {
      val request = FakeRequest(POST, "/start-game")
      val result = route(app, request).get

      status(result) mustBe BAD_REQUEST
    }

    "respond with 200 to a POST with a valid payload" in {
      val gameInfo = Map("pointsToWin" -> "1000", "maxRounds" -> "2000", "dynamiteCount" -> "100")
      val request = FakeRequest(POST, "/start-game").withJsonBody(Json.toJson(gameInfo))
      val result = route(app, request).get

      status(result) mustBe OK
    }

    "respond with 400 to a POST with an invalid payload" in {
      val gameInfo = Map("pointsToWin" -> "1000", "maxRounds" -> "2000", "dynamiteCount" -> "abc")
      val request = FakeRequest(POST, "/start-game").withJsonBody(Json.toJson(gameInfo))
      val result = route(app, request).get

      status(result) mustBe BAD_REQUEST
    }

  }
}
