package connectors

import actors._
import com.google.inject.ImplementedBy
import models.GameParameters

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@ImplementedBy(classOf[RealBotConnector])
trait BotConnector {
  def start(url: String, gameParameters: GameParameters): Future[ConnectorMessage]

  def getMove(url: String): Future[ConnectorMessage]

  def reportMove(url: String, move: Move): Future[ConnectorMessage]
}

class RealBotConnector extends BotConnector {
  override def start(botBaseUrl: String, gameParameters: GameParameters): Future[ConnectorMessage] = {
    Future(StartSucceeded)
  }

  override def getMove(botBaseUrl: String): Future[ConnectorMessage] = {
    Future(GotMove(Rock))
  }

  override def reportMove(url: String, move: Move): Future[ConnectorMessage] = {
    Future(ReportMoveSucceeded)
  }
}

sealed trait ConnectorMessage
case object StartSucceeded extends ConnectorMessage
case object StartFailed extends ConnectorMessage
case class GotMove(move: Move) extends ConnectorMessage
case object GetMoveFailed extends ConnectorMessage
case object ReportMoveSucceeded extends ConnectorMessage
case object ReportMoveFailed extends ConnectorMessage
