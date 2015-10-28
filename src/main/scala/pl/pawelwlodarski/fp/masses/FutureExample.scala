package pl.pawelwlodarski.fp.masses

import scala.concurrent.Future

/**
 * Created by pawel on 28.10.15.
 */
object FutureExample {

  val f1: Double => Double = x=>x+1.0
  val f2: Double => Int= x => Math.ceil(x).toInt
  val f3: Int => String = x =>  "LOG : "+x

  def main(args: Array[String]) {
    import scala.concurrent.ExecutionContext.Implicits.global
    val f=Future {
      Thread.sleep(1000)
      1.0
    }.map(f1).map(f2).map(f3).onSuccess{case s=>println(s)}
    Thread.sleep(2000)
  }
}
