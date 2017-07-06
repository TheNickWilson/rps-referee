package actors

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import models.GameInfo
import play.api.Logger

class Referee(bot1Maker: ActorRefFactory => ActorRef, bot2Maker: ActorRefFactory => ActorRef, gameInfo: GameInfo) extends Actor {
  val bot1 = bot1Maker(context)
  val bot2 = bot2Maker(context)
  val gameParameters = gameInfo.gameParameters

  var bot1State: BotState = NotStarted
  var bot2State: BotState = NotStarted

  override def receive: Receive = {
    case StartGame =>
      bot1 ! Start(gameInfo.bot1, gameParameters)
      bot2 ! Start(gameInfo.bot2, gameParameters)
    case Started =>
      if (sender == bot1) bot1State = Waiting else bot2State = Waiting
      if (bot1State == Waiting && bot2State == Waiting) {
        bot1 ! GetMove
        bot2 ! GetMove
      }
    case FailedToStart =>
      Logger.debug(s"$sender failed to start, shutting down system")
      // TODO: Shut down
  }
}

sealed trait RefereeMessage
case object StartGame extends RefereeMessage

sealed trait BotState
case object NotStarted extends BotState
case object Waiting extends BotState