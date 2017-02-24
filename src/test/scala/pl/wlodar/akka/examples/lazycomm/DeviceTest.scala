package pl.wlodar.akka.examples.lazycomm

import java.util.Base64

import org.scalatest.{MustMatchers, WordSpec}
import pl.wlodar.akka.examples.lazycomm.Device.{CommunicationStart, ElementMessage}

class DeviceTest extends WordSpec with MustMatchers{

  "Device" should {
    "handle full conversation" in {
      val message1="text for"
      val message2="testing device"
      val message3="conversation"

      val CommunicationStart(sessionId,_) =Device.openChannel()

      Device.send(ElementMessage(sessionId,encode(message1)))
      Device.send(ElementMessage(sessionId,encode(message2)))
      Device.send(ElementMessage(sessionId,encode(message3)))

      val result=Device.endCommunication(sessionId)

      result mustBe "text for testing device conversation"
    }
  }

  def encode(message:String):String = Base64.getEncoder.encodeToString(message.getBytes)

}
