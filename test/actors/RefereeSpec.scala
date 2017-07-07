package actors

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import models.{BotInfo, GameInfo, GameParameters}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._

class RefereeSpec extends TestKit(ActorSystem("RefereeSpec")) with ImplicitSender with WordSpecLike with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val bot1Info = BotInfo("Bot 1", "http://bot1")
  val bot2Info = BotInfo("Bot 2", "http://bot2")
  val gameParams = GameParameters(1, 2, 3)
  val gameInfo = GameInfo(gameParams, bot1Info, bot2Info)

  val probe1 = TestProbe()
  val probe2 = TestProbe()
  val bot1Maker = (_: ActorRefFactory) => probe1.ref
  val bot2Maker = (_: ActorRefFactory) => probe2.ref

  "A referee actor" must {
    "tell two bot actors to start when starting a new game" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      referee ! StartGame

      probe1.expectMsg(Start(bot1Info, gameParams))
      probe2.expectMsg(Start(bot2Info, gameParams))
    }

    // TODO: Remove when we're confident this behaviour is correct, pausing for 30ms sucks!
    "not send GetMove messages when only one bot has started" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, Started)

      probe1.expectNoMsg(30 millis)
      probe2.expectNoMsg(30 millis)
    }

    "send a GetMove message to both bots when both have started" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, Started)
      probe2.send(referee, Started)

      probe1.expectMsg(GetMove)
      probe2.expectMsg(GetMove)
    }

    // TODO: Remove when we're confident this behaviour is correct, pausing for 30ms sucks!
    "not report moves when only one bot has played" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, PlayedMove(Rock))

      probe1.expectNoMsg(30 millis)
      probe2.expectNoMsg(30 millis)
    }

    "tell both bots their opponents' moves after both have played" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, PlayedMove(Rock))
      probe2.send(referee, PlayedMove(Paper))

      probe1.expectMsg(ReportMove(Paper))
      probe2.expectMsg(ReportMove(Rock))
    }
  }
}
