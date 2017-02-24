package pl.wlodar.akka.examples.lazycomm

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.stream.scaladsl.Source
import pl.wlodar.akka.examples.lazycomm.DeviceClient.{DeviceRequest, DeviceResponse}

//http://erikerlandson.github.io/blog/2015/03/31/hygienic-closures-for-scala-function-serialization/
object LazyCommunication {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("LazyCommunication")

    val deviceClient = system.actorOf(Props[DeviceClient],"deviceClient")

    val businessClient = system.actorOf(Props(new BusinessClient(deviceClient)),"businessClient")
    system.eventStream.subscribe(businessClient,classOf[DeviceResponse])

    businessClient ! "START"

    TimeUnit.SECONDS.sleep(5)
    system.terminate()
  }

}

class BusinessClient(deviceClient: ActorRef) extends Actor with ActorLogging{
  override def receive: Receive = {
    case "START" =>
      val text = "this$is$the$sentence$which$will$be$encoded"

      val parts: LoggingAdapter=> Int => Source[String,NotUsed] = log => maxSize => {
        val fragments=BusinessLogicSplit.split("\\$")(text,maxSize)
        log.info("Business split into {}",fragments)
        Source(fragments.toList)
            .mapAsync(parallelism = fragments.size)(RemoteEncoderSimulation.encode)
      }

      deviceClient ! DeviceRequest(parts)

    case DeviceResponse(result) =>
      println(s"Business client result : $result")
  }
}


