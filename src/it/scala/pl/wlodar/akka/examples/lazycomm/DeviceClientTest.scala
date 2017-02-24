package pl.wlodar.akka.examples.lazycomm

import akka.actor.Props
import akka.event.LoggingAdapter
import akka.stream.scaladsl.Source
import akka.testkit.TestProbe
import pl.wlodar.akka.examples.lazycomm.DeviceClient.{DeviceRequest, DeviceResponse}
import pl.wlodar.akka.test.StandardActorTest

import scala.concurrent.{Await, Future}

/**
  * pl.wlodar.akka.examples.lazycomm.DeviceClientTest$$Lambda$289/1204481453@7139992f
  * pl.wlodar.akka.examples.lazycomm.DeviceClientTest$$Lambda$289/1204481453@7139992f
  * http://stackoverflow.com/questions/20108445/does-akka-support-in-process-messaging-without-object-serialization
  */
class DeviceClientTest extends StandardActorTest{

  import scala.concurrent.duration._

  implicit val ec=system.dispatcher

  "Device Client" should {
    "send all parts to device correctly" in {
      val probe=TestProbe()
      system.eventStream.subscribe(probe.ref,classOf[DeviceResponse])
      val deviceClient=system.actorOf(Props[DeviceClient])

      val m1=RemoteEncoderSimulation.encode("first")
      val m2=RemoteEncoderSimulation.encode("second")
      val m3=RemoteEncoderSimulation.encode("third")

      val parts=Await.result(Future.sequence(List(m1,m2,m3)), 2.seconds)

      val partsProvider=(_:LoggingAdapter)=>(_:Int) => Source(parts)

      deviceClient ! DeviceRequest(partsProvider)

      val response=probe.expectMsgClass(classOf[DeviceResponse])

      response.text mustBe "first second third"
    }
  }

}
