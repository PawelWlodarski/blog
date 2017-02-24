package pl.wlodar.akka.test

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
  * Created by pawel on 25.08.16.
  */
trait StopSystemAfterAll extends BeforeAndAfterAll{this:TestKit with Suite =>

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}
