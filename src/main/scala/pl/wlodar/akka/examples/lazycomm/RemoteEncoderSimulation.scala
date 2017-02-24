package pl.wlodar.akka.examples.lazycomm

import java.util.Base64
import java.util.concurrent.{Executors, TimeUnit}

import scala.concurrent.{ExecutionContext, Future}

object RemoteEncoderSimulation {

  private implicit val encodingEC = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))

  def encode(text:String):Future[String] = Future{
    TimeUnit.MILLISECONDS.sleep(1)
    Base64.getEncoder.encodeToString(text.getBytes)
  }

}