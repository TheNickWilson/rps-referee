package forms

import org.scalatestplus.play.guice.GuiceOneAppPerTest
import forms.StartGameForm.startGameForm
import models.{Bot, GameInfo}

class StartGameFormSpec extends FormSpec with GuiceOneAppPerTest {
  val validGameInfo = Map(
    "pointsToWin" -> "1000",
    "maxRounds" -> "2000",
    "dynamiteCount" -> "100",
    "bot1.name" -> "Bot 1",
    "bot1.url" -> "http://bot1",
    "bot2.name" -> "Bot 2",
    "bot2.url" -> "http://bot2")

  "StartGameForm" should {
    "bind correctly when given a valid payload" in {
      val form = startGameForm.bind(validGameInfo)
      form.get mustBe GameInfo(1000, 2000, 100, Bot("Bot 1", "http://bot1"), Bot("Bot 2", "http://bot2"))
    }

    "fail to bind when pointsToWin is omitted" in {
      val data = validGameInfo - "pointsToWin"
      val expectedErrors = error("pointsToWin", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when pointsToWin is non-numeric" in {
      val data = validGameInfo + ("pointsToWin" -> "not a number")
      val expectedErrors = error("pointsToWin", "error.number")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when maxRounds is omitted" in {
      val data = validGameInfo - "maxRounds"
      val expectedErrors = error("maxRounds", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when maxRounds is non-numeric" in {
      val data = validGameInfo + ("maxRounds" -> "not a number")
      val expectedErrors = error("maxRounds", "error.number")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when dynamiteCount is omitted" in {
      val data = validGameInfo - "dynamiteCount"
      val expectedErrors = error("dynamiteCount", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when dynamiteCount is non-numeric" in {
      val data = validGameInfo + ("dynamiteCount" -> "not a number")
      val expectedErrors = error("dynamiteCount", "error.number")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when bot1.name is omitted" in {
      val data = validGameInfo - "bot1.name"
      val expectedErrors = error("bot1.name", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when bot1.url is omitted" in {
      val data = validGameInfo - "bot1.url"
      val expectedErrors = error("bot1.url", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when bot2.name is omitted" in {
      val data = validGameInfo - "bot2.name"
      val expectedErrors = error("bot2.name", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when bot2.url is omitted" in {
      val data = validGameInfo - "bot2.url"
      val expectedErrors = error("bot2.url", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

  }
}
