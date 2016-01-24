package pl.pawelwlodarski.separation

import scala.io.StdIn


object SeparateConsoleUI extends UIWithLabels with Logger{

  override type ActionResult = Unit

  def main(args: Array[String]) {
      display.foreach(println)
      val trigger=StdIn.readInt()
      log(s"you hit $trigger")
      executeAction(trigger)
        .getOrElse(println(s"there is no option $trigger"))
  }

  override def invokeApplicationAction1(): Unit = log("create folders")
  override def invokeApplicationAction2(): Unit = log("move data")
}


trait UI{
  type ActionResult

  sealed trait UIAction{
    def execute:ActionResult
  }

  object Action1 extends UIAction{
    override def execute: ActionResult = invokeApplicationAction1()
  }

  object Action2 extends UIAction{
    override def execute: ActionResult = invokeApplicationAction2()
  }

  lazy val triggersMappings:Map[Int,UIAction]= Map(
    1 -> Action1,
    2 -> Action2
  )

  def executeAction(trigger:Int):Option[ActionResult]={
      triggersMappings.get(trigger).map(a=>a.execute)
  }

  def display:List[String]={
    triggersMappings.toList.sortBy(_._1).map{case (k,v)=>s"$k : ${labels(v)}"}
  }


  def labels:Map[UIAction,String]


  def invokeApplicationAction1():ActionResult
  def invokeApplicationAction2():ActionResult
}

trait UIWithLabels extends UI{

  def labels:Map[UIAction,String]=Map(
    Action1 -> "Action1 - create folders",
    Action2 -> "Action2 - move data"
  )
}

trait Logger{
  def log(s:String)=println(s)
}