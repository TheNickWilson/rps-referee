package actors

import akka.actor.{ActorRef, ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import models.{BotInfo, GameInfo, GameParameters}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, BeforeAndAfterEach, WordSpecLike}

import scala.concurrent.duration._

class RefereeSpec extends TestKit(ActorSystem("RefereeSpec")) with ImplicitSender with WordSpecLike with BeforeAndAfterAll with BeforeAndAfterEach {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  override def beforeEach() = {
    probe1 = TestProbe()
    probe2 = TestProbe()
  }

  val bot1Info = BotInfo("Bot 1", "http://bot1")
  val bot2Info = BotInfo("Bot 2", "http://bot2")
  val gameParams = GameParameters(1, 2, 3)
  val gameInfo = GameInfo(gameParams, bot1Info, bot2Info)

  var probe1 = TestProbe()
  var probe2 = TestProbe()
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

    "increment the number of rounds played when both bots have acknowledged their opponent's move" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      referee ! GetRoundsPlayed
      expectMsg(RoundsPlayed(0))

      probe1.send(referee, ReportedSuccessfully)
      probe2.send(referee, ReportedSuccessfully)

      referee ! GetRoundsPlayed
      expectMsg(RoundsPlayed(1))
    }

    "record the score correctly when Bot 1 wins" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, PlayedMove(Paper))
      probe2.send(referee, PlayedMove(Rock))

      referee ! GetScore
      expectMsg(Score(1, 0))
    }

    "record the score correctly when Bot 2 wins" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, PlayedMove(Rock))
      probe2.send(referee, PlayedMove(Paper))

      referee ! GetScore
      expectMsg(Score(0, 1))
    }

    "play another round when both bots have acknowledged their opponent's move" in {
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, gameInfo)))
      probe1.send(referee, ReportedSuccessfully)
      probe2.send(referee, ReportedSuccessfully)

      probe1.expectMsg(GetMove)
      probe2.expectMsg(GetMove)
    }

    // TODO: Set up test probes to mimic the proper behaviour
    "stop playing when the maximum number of rounds has been played" in {
      val oneRoundGame = gameInfo copy (gameParameters = GameParameters(1, 1, 1))
      val referee = system.actorOf(Props(new Referee(bot1Maker, bot2Maker, oneRoundGame)))

      probe1.setAutoPilot(new TestActor.AutoPilot{
        def run(sender: ActorRef, msg: Any) = msg match {
          case ReportMove(_) =>
            sender ! ReportedSuccessfully
            TestActor.KeepRunning
        }
      })

      probe2.setAutoPilot(new TestActor.AutoPilot{
        def run(sender: ActorRef, msg: Any) = msg match {
          case ReportMove(_) =>
            sender ! ReportedSuccessfully
            TestActor.KeepRunning
        }
      })

      probe1.send(referee, PlayedMove(Rock))
      probe2.send(referee, PlayedMove(Paper))

      probe1.expectMsg(ReportMove(Paper))
      probe2.expectMsg(ReportMove(Rock))

      probe1.expectNoMsg(30 millis)
      probe2.expectNoMsg(30 millis)
    }
  }
}
