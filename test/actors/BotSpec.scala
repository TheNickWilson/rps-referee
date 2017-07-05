package actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import connectors._
import models.{BotInfo, GameInfo, GameParameters}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class BotSpec extends TestKit(ActorSystem("BotSpec")) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val botInfo = BotInfo("Bot 1", "http://bot1")
  val gameParams = GameParameters(1, 2, 3)


  "A bot actor" must {
    "send back a Started message when asked to start" in {
      val bot = system.actorOf(Props(new Bot(WorkingBotConnector)))
      bot ! Start(botInfo, gameParams)
      expectMsg(Started)
    }

    "send back a FailedToStart message when asked to start but the remote API fails" in {
      val bot = system.actorOf(Props(new Bot(FailingToStartConnector)))
      bot ! Start(botInfo, gameParams)
      expectMsg(FailedToStart)
    }

    "send back the move that was played when asked to get a move" in {
      val bot = system.actorOf(Props(new Bot(WorkingBotConnector)))
      bot ! Start(botInfo, gameParams)
      receiveOne(1.seconds)
      bot ! GetMove
      expectMsg(PlayedMove(Rock))
    }

    "send back a FailedToGetMove message when asked to get a move but the remote API fails" in {
      val bot = system.actorOf(Props(new Bot(FailingToPlayMoveConnector)))
      bot ! Start(botInfo, gameParams)
      receiveOne(1.seconds)
      bot ! GetMove
      expectMsg(FailedToGetMove)
    }

    "send back a ReportedMove message when asked to tell the bot its opponent's move" in {
      val bot = system.actorOf(Props(new Bot(WorkingBotConnector)))
      bot ! Start(botInfo, gameParams)
      receiveOne(1.seconds)
      bot ! ReportMove(Rock)
      expectMsg(ReportedSuccessfully)
    }

    "send back a FailedToReportMove message when asked to tell the bot its opponent's move but the remote API fails" in {
      val bot = system.actorOf(Props(new Bot(FailingToReportMoveConnector)))
      bot ! Start(botInfo, gameParams)
      receiveOne(1.seconds)
      bot ! ReportMove(Rock)
      expectMsg(FailedToReportMove)
    }
  }
}

object FailingToStartConnector extends FakeBotConnector {
  override def start(url: String, gameParameters: GameParameters) = Future(StartFailed)
}

object WorkingBotConnector extends FakeBotConnector {
  override def getMove(url: String) = Future(GotMove(Rock))
  override def reportMove(url: String, move: Move) = Future(ReportMoveSucceeded)
}

object FailingToPlayMoveConnector extends FakeBotConnector {
  override def getMove(url: String): Future[ConnectorMessage] = Future(GetMoveFailed)
}

object FailingToReportMoveConnector extends FakeBotConnector {
  override def reportMove(url: String, move: Move) = Future(ReportMoveFailed)
}

trait FakeBotConnector extends BotConnector {
  override def start(url: String, gameParameters: GameParameters): Future[ConnectorMessage] = Future(StartSucceeded)
  override def getMove(url: String): Future[ConnectorMessage] = ???
  override def reportMove(url: String, move: Move): Future[ConnectorMessage] = ???
}