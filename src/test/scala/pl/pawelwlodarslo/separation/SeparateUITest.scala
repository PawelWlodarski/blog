package pl.pawelwlodarslo.separation

import org.scalatest.{Matchers, WordSpec}
import pl.pawelwlodarski.separation.UIWithLabels

class SeparateUITest extends WordSpec with Matchers{

  "UI" should{
    "display all options" in {
      val result: List[String] =UnitTestUI.display

      result should contain only(
        "1 : Label for action1",
        "2 : Label for action2")
    }
  }

}


object UnitTestUI extends UIWithLabels{
  override type ActionResult = this.type
  override def invokeApplicationAction2(): UnitTestUI.ActionResult = ???
  override def invokeApplicationAction1(): UnitTestUI.ActionResult = ???
}


