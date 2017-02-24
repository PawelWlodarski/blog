package pl.wlodar.akka.examples.lazycomm

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingAdapter
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import pl.wlodar.akka.examples.lazycomm.Device.{CommunicationStart, ElementMessage}
import pl.wlodar.akka.examples.lazycomm.DeviceClient._

import scala.concurrent.Future

class DeviceClient extends Actor with ActorLogging{

  implicit val ec=context.dispatcher
  implicit val materializer=ActorMaterializer()

  val deviceSink = (sessionId:Int) =>  Sink.foreach[String]{e=>
    Device.send(ElementMessage(sessionId,e))
  }

  override def receive: Receive = {
    case DeviceRequest(parts) =>
      log.info("received device request")
      val CommunicationStart(sessionId,maxLength)=Device.openChannel()
      val result: Future[Done] =parts(log)(maxLength).runWith(deviceSink(sessionId))

      result.map(_ => End(sessionId)) pipeTo self

    case End(sessionId) =>
      log.info("session with id {} ended",sessionId)
      val result=Device.endCommunication(sessionId)
      context.system.eventStream.publish(DeviceResponse(result))
  }
}

object DeviceClient {
  val NAME = "DeviceClient"
  type DeviceRequestParts = LoggingAdapter => Int => Source[String,NotUsed]
  /**
    *
    * @param parts (macLength) => Source producing parts to send
    */
  case class DeviceRequest(parts : DeviceRequestParts)
  case class DeviceResponse(text:String)
  private case class End(sessionId:Int)
}