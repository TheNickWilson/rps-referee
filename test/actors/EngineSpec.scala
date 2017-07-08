package actors

import org.scalatestplus.play.PlaySpec

class EngineSpec extends PlaySpec {

  "Game engine" when {
    "determining the winner of a round" must {
      "return Draw for a game of Rock - Rock" in {
        Engine.determineResult(Rock, Rock) mustBe Draw
      }

      "return Bot2Wins for a game of Rock - Paper" in {
        Engine.determineResult(Rock, Paper) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Rock - Scissors" in {
        Engine.determineResult(Rock, Scissors) mustBe Bot1Wins
      }

      "return Bot2Wins for a game of Rock - Dynamite" in {
        Engine.determineResult(Rock, Dynamite) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Rock - Waterbomb" in {
        Engine.determineResult(Rock, Waterbomb) mustBe Bot1Wins
      }

      "return Bot1Wins for a game of Paper - Rock" in {
        Engine.determineResult(Paper, Rock) mustBe Bot1Wins
      }

      "return Draw for a game of Paper - Paper" in {
        Engine.determineResult(Paper, Paper) mustBe Draw
      }

      "return Bot2Wins for a game of Paper - Scissors" in {
        Engine.determineResult(Paper, Scissors) mustBe Bot2Wins
      }

      "return Bot2Wins for a game of Paper - Dynamite" in {
        Engine.determineResult(Paper, Dynamite) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Paper - Waterbomb" in {
        Engine.determineResult(Paper, Waterbomb) mustBe Bot1Wins
      }

      "return Bot2Wins for a game of Scissors - Rock" in {
        Engine.determineResult(Scissors, Rock) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Scissors - Paper" in {
        Engine.determineResult(Scissors, Paper) mustBe Bot1Wins
      }

      "return Draw for a game of Scissors - Scissors" in {
        Engine.determineResult(Scissors, Scissors) mustBe Draw
      }

      "return Bot2Wins for a game of Scissors - Dynamite" in {
        Engine.determineResult(Scissors, Dynamite) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Scissors - Waterbomb" in {
        Engine.determineResult(Scissors, Waterbomb) mustBe Bot1Wins
      }

      "return Bot1Wins for a game of Dynamite - Rock" in {
        Engine.determineResult(Dynamite, Rock) mustBe Bot1Wins
      }

      "return Bot1Wins for a game of Dynamite - Paper" in {
        Engine.determineResult(Dynamite, Paper) mustBe Bot1Wins
      }

      "return Bot1Wins for a game of Dynamite - Scissors" in {
        Engine.determineResult(Dynamite, Scissors) mustBe Bot1Wins
      }

      "return Draw for a game of Dynamite - Dynamite" in {
        Engine.determineResult(Dynamite, Dynamite) mustBe Draw
      }

      "return Bot2Wins for a game of Dynamite - Waterbomb" in {
        Engine.determineResult(Dynamite, Waterbomb) mustBe Bot2Wins
      }

      "return Bot2Wins for a game of Waterbomb - Rock" in {
        Engine.determineResult(Waterbomb, Rock) mustBe Bot2Wins
      }

      "return Bot2Wins for a game of Waterbomb - Paper" in {
        Engine.determineResult(Waterbomb, Paper) mustBe Bot2Wins
      }

      "return Bot2Wins for a game of Waterbomb - Scissors" in {
        Engine.determineResult(Waterbomb, Scissors) mustBe Bot2Wins
      }

      "return Bot1Wins for a game of Waterbomb - Dynamite" in {
        Engine.determineResult(Waterbomb, Dynamite) mustBe Bot1Wins
      }

      "return Draw for a game of Waterbomb - Waterbomb" in {
        Engine.determineResult(Waterbomb, Waterbomb) mustBe Draw
      }
    }
  }
}
