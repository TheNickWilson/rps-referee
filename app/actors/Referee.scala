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
      Logger.debug(s"$sender failed to start, shutting down system")
      // TODO: Shut down
    case PlayedMove(move) =>
      if (sender == bot1) {
        bot1State = BotState(Moved, move)
      } else {
        bot2State =  BotState(Moved, move)
      }
      if (bot1State.currentState == Moved && bot2State.currentState == Moved) {
        bot1 ! ReportMove(bot2State.lastMove)
        bot2 ! ReportMove(bot1State.lastMove)
      }
  }
}

sealed trait RefereeMessage
case object StartGame extends RefereeMessage

sealed trait CurrentState
case object NotStarted extends CurrentState
case object Waiting extends CurrentState
case object Moved extends CurrentState

case class BotState(var currentState: CurrentState, var lastMove: Move)