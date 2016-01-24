package pl.pawelwlodarslo.separation

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks
import pl.pawelwlodarski.separation.UIWithLabels

/**
  * Created by pawel on 24.01.16.
  */
class UISpec extends PropSpec with TableDrivenPropertyChecks with Matchers{

  val actions=UIForPropertyTesting.triggersMappings.toList
  val actionsTable=Table("actions",actions : _*)

  property("aaa"){
    forAll(actionsTable){ case (trigger,expectedAction)=>
      val result=UIForPropertyTesting.executeAction(trigger)

      result shouldBe Some(expectedAction)
    }
  }
}

object UIForPropertyTesting extends UIWithLabels{
  override type ActionResult = UIAction
  override def invokeApplicationAction1(): UIAction=  Action1
  override def invokeApplicationAction2(): UIAction = Action2
}
