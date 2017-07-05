package forms

import org.scalatestplus.play.guice.GuiceOneAppPerTest
import forms.StartGameForm.startGameForm
import models.{BotInfo, GameInfo, GameParameters}

class StartGameFormSpec extends FormSpec with GuiceOneAppPerTest {
  val validGameInfo = Map(
    "gameParameters.pointsToWin" -> "1000",
    "gameParameters.maxRounds" -> "2000",
    "gameParameters.dynamiteCount" -> "100",
    "bot1.name" -> "Bot 1",
    "bot1.url" -> "http://bot1",
    "bot2.name" -> "Bot 2",
    "bot2.url" -> "http://bot2")

  "StartGameForm" should {
    "bind correctly when given a valid payload" in {
      val form = startGameForm.bind(validGameInfo)
      form.get mustBe GameInfo(GameParameters(1000, 2000, 100), BotInfo("Bot 1", "http://bot1"), BotInfo("Bot 2", "http://bot2"))
    }

    "fail to bind when pointsToWin is omitted" in {
      val data = validGameInfo - "gameParameters.pointsToWin"
      val expectedErrors = error("gameParameters.pointsToWin", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when pointsToWin is non-numeric" in {
      val data = validGameInfo + ("gameParameters.pointsToWin" -> "not a number")
      val expectedErrors = error("gameParameters.pointsToWin", "error.number")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when maxRounds is omitted" in {
      val data = validGameInfo - "gameParameters.maxRounds"
      val expectedErrors = error("gameParameters.maxRounds", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when maxRounds is non-numeric" in {
      val data = validGameInfo + ("gameParameters.maxRounds" -> "not a number")
      val expectedErrors = error("gameParameters.maxRounds", "error.number")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when dynamiteCount is omitted" in {
      val data = validGameInfo - "gameParameters.dynamiteCount"
      val expectedErrors = error("gameParameters.dynamiteCount", "error.required")
      checkForError(startGameForm, data, expectedErrors)
    }

    "fail to bind when dynamiteCount is non-numeric" in {
      val data = validGameInfo + ("gameParameters.dynamiteCount" -> "not a number")
      val expectedErrors = error("gameParameters.dynamiteCount", "error.number")
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
