package pl.wlodar.akka.examples.lazycomm

import org.scalatest.{MustMatchers, WordSpec}

class BusinessLogicSplitTest extends WordSpec with MustMatchers{

  "Business Logic Split" should {
    "split text to elements no longer that max length" in {
      val text="this$is$sentence$which$will$be$encoded"

      val result=BusinessLogicSplit.split("\\$")(text,15)

      result mustBe List(" this is","sentence which","will be encoded")
    }
  }

}
