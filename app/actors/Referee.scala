package actors

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import models.GameInfo
import play.api.Logger

class Referee(bot1Maker: ActorRefFactory => ActorRef, bot2Maker: ActorRefFactory => ActorRef, gameInfo: GameInfo) extends Actor {
  val bot1 = bot1Maker(context)
  val bot2 = bot2Maker(context)
  val gameParameters = gameInfo.gameParameters

  var bot1State: BotState = BotState(NotStarted, NoMove)
  var bot2State = BotState(NotStarted, NoMove)

  var roundsPlayed = 0
  var score = Score(0, 0)

  override def receive: Receive = {
    case StartGame =>
      bot1 ! Start(gameInfo.bot1, gameParameters)
      bot2 ! Start(gameInfo.bot2, gameParameters)

    case Started =>
      if (sender == bot1) bot1State.currentState = Waiting else bot2State.currentState = Waiting
      if (bot1State.currentState == Waiting && bot2State.currentState == Waiting) {
        bot1 ! GetMove
        bot2 ! GetMove
      }

    case FailedToStart =>
      Logger.debug(s"$sender failed to start, shutting down")
    // TODO: Shut down

    case FailedToGetMove =>
      Logger.debug(s"$sender failed to get a move, shutting down")
    // TODO: Shut down

    case FailedToReportMove =>
      Logger.debug(s"$sender failed to report a move, shutting down")
    case PlayedMove(move) =>
      if (sender == bot1) {
        bot1State = BotState(Moved, move)
      } else {
        bot2State =  BotState(Moved, move)
      }
      if (bot1State.currentState == Moved && bot2State.currentState == Moved) {
        Engine.determineResult(bot1State.lastMove, bot2State.lastMove) match {
          case Bot1Wins => score = score copy (bot1 = score.bot1 + 1)
          case Bot2Wins => score = score copy (bot2 = score.bot2 + 1)
          case Draw => ???
        }
        bot1 ! ReportMove(bot2State.lastMove)
        bot2 ! ReportMove(bot1State.lastMove)
      }

    case ReportedSuccessfully =>
      if (sender == bot1) bot1State.currentState = AcknolwedgedLastMove else bot2State.currentState = AcknolwedgedLastMove
      if (bot1State.currentState == AcknolwedgedLastMove && bot2State.currentState == AcknolwedgedLastMove) {
        roundsPlayed += 1
        bot1 ! GetMove
        bot2 ! GetMove
      }

    case GetRoundsPlayed =>
      sender ! RoundsPlayed(roundsPlayed)
    case GetScore =>
      sender ! score
  }
}

sealed trait RefereeMessage
case object StartGame extends RefereeMessage
case object GetRoundsPlayed extends RefereeMessage
case class RoundsPlayed(rounds: Int) extends RefereeMessage
case object GetScore extends RefereeMessage
case class Score(bot1: Int, bot2: Int) extends RefereeMessage

sealed trait CurrentState
case object NotStarted extends CurrentState
case object Waiting extends CurrentState
case object Moved extends CurrentState
case object AcknolwedgedLastMove extends CurrentState

case class BotState(var currentState: CurrentState, var lastMove: Move)

object Engine {
  def determineResult(bot1Move: Move, bot2Move: Move): ResultOfRound = {
    if (bot1Move == bot2Move) Draw
    else bot1Move match {
      case Rock =>
        if (bot2Move == Paper || bot2Move == Dynamite) Bot2Wins else Bot1Wins
      case Paper =>
        if (bot2Move == Scissors || bot2Move == Dynamite) Bot2Wins else Bot1Wins
      case Scissors =>
        if (bot2Move == Rock || bot2Move == Dynamite) Bot2Wins else Bot1Wins
      case Dynamite =>
        if (bot2Move == Waterbomb) Bot2Wins else Bot1Wins
      case Waterbomb =>
        if (bot2Move == Dynamite) Bot1Wins else Bot2Wins
      case _ =>
        Draw
    }
  }
}

sealed trait ResultOfRound
case object Bot1Wins extends ResultOfRound
case object Bot2Wins extends ResultOfRound
case object Draw extends ResultOfRound