package connectors

import actors._
import com.google.inject.ImplementedBy
import models.GameParameters
import play.api.Logger
import play.api.libs.json.Json
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.http.ws._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[RealBotConnector])
trait BotConnector {
  def start(url: String, gameParameters: GameParameters): Future[ConnectorMessage]

  def getMove(url: String): Future[ConnectorMessage]
}

class RealBotConnector extends BotConnector {
  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def start(botBaseUrl: String, gameParameters: GameParameters): Future[ConnectorMessage] = {
    val http = new WSGet with WSPost {
      override val hooks: Seq[HttpHook] = NoneRequired
    }

    val json = Json.toJson(gameParameters)
    Logger.debug(s"About to send $json to $botBaseUrl/start")
    http.POST(s"$botBaseUrl/start", "please start").map { response =>
      response.status match {
        case 200 =>
          Logger.debug(s"Call to $botBaseUrl/start succeeded")
          StartSucceeded
        case otherCode =>
          Logger.debug(s"Call to $botBaseUrl/start returned error code $otherCode")
          StartFailed
      }
    }
  }

  override def getMove(botBaseUrl: String): Future[ConnectorMessage] = {
    val http = new WSGet with WSPost {
      override val hooks: Seq[HttpHook] = NoneRequired
    }

    http.GET(s"$botBaseUrl/move").map { response =>
      response.status match {
        case 200 =>
          Logger.debug(s"Call to $botBaseUrl/move succeeded and returned ${response.body}")
          response.body match {
            case "ROCK" => GotMove(Rock)
            case "PAPER" => GotMove(Paper)
            case "SCISSORS" => GotMove(Scissors)
            case "DYNAMITE" => GotMove(Dynamite)
            case "WATERBOMB" => GotMove(Waterbomb)
            case x =>
              Logger.debug(s"Got the invalid move $x from $botBaseUrl/move")
              GetMoveFailed
          }
        case otherCode =>
          Logger.debug(s"Call to $botBaseUrl/move returned error code $otherCode")
          GetMoveFailed
      }
    }
  }
}

sealed trait ConnectorMessage
case object StartSucceeded extends ConnectorMessage
case object StartFailed extends ConnectorMessage
case class GotMove(move: Move) extends ConnectorMessage
case object GetMoveFailed extends ConnectorMessage