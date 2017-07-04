package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
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
  }
}
