package actors

import akka.actor.{Actor, ActorRef}
import akka.pattern.pipe
import connectors._
import models.{BotInfo, GameParameters}

class Bot(botConnector: BotConnector) extends Actor {
  import context.dispatcher

  var referee: ActorRef = _
  var info: BotInfo = _

  override def receive: Receive = {
    case Start(botInfo, gameParams) =>
      referee = sender
      info = botInfo
      botConnector.start(info.url, gameParams) pipeTo self
    case StartSucceeded =>
      referee ! Started
    case StartFailed =>
      referee ! FailedToStart
    case GetMove =>
      botConnector.getMove(info.url) pipeTo self
    case GotMove(m) =>
      referee ! PlayedMove(m)
    case GetMoveFailed =>
      referee ! FailedToGetMove
  }
}

sealed trait BotMessage
case class Start(botInfo: BotInfo, gameParams: GameParameters) extends BotMessage
case object Started extends BotMessage
case object FailedToStart extends BotMessage
case object GetMove extends BotMessage
case object FailedToGetMove extends BotMessage
case class PlayedMove(move: Move) extends BotMessage

sealed trait Move
case object Rock extends Move
case object Paper extends Move
case object Scissors extends Move
case object Dynamite extends Move
case object Waterbomb extends Move