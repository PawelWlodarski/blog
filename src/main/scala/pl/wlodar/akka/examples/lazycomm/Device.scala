package pl.wlodar.akka.examples.lazycomm

import java.util.Base64

import org.slf4j.LoggerFactory

object Device {

  val logger=LoggerFactory.getLogger(Device.getClass)

  type SessionData = List[String]
  private val MAX_SIZE = 15

  private var sessions: Map[Int, SessionData] = Map()

  private var sessionCounter = 0


  def openChannel(): CommunicationStart = {
    sessionCounter = sessionCounter + 1
    val sessionId = sessionCounter
    sessions = sessions + (sessionId -> List())
    CommunicationStart(sessionId, MAX_SIZE)
  }

  def send(e: ElementMessage): Unit = {
    logger.info("received {}",e)
    val decoded=new String(Base64.getDecoder.decode(e.element))
    require(decoded.length <= MAX_SIZE, s"'$decoded' is to long")
    val newState = decoded :: sessions(e.sessionId)
    sessions = sessions + (e.sessionId -> newState)
  }

  def endCommunication(sessionId: Int): String = {
    val communicationResult = sessions(sessionId).reverse.mkString(" ")
  communicationResult
  }


  case class CommunicationStart(sessionId: Int, maxLength: Int)

  case class ElementMessage(sessionId: Int, element: String)

}
