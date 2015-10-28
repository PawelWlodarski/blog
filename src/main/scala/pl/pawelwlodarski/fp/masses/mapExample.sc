import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.util.Try
val f1: Double => Double = x=>x+1.0
val f2: Double => Int= x => Math.ceil(x).toInt
val f3: Int => String = x =>  "LOG : "+x

Some(1.0).map(f1).map(f2).map(f3)
//Option[String] = Some(LOG : 2)
None.map(f1).map(f2).map(f3)
//Option[String] = None

Try(4.0/2.0).map(f1).map(f2).map(f3)
//res2: scala.util.Try[String] = Success(LOG : 3)
Try((4/0).toDouble).map(f1).map(f2).map(f3)
// scala.util.Try[String] = Failure(java.lang.ArithmeticException: / by zero)





List(1.0,2.0,3.0).map(f1).map(f2).map(f3)
//res4: List[String] = List(LOG : 2, LOG : 3, LOG : 4)

def map[A,B](f:A=>B)(source:Seq[A]): Seq[B] = {
  val buffer:scala.collection.mutable.ListBuffer[B]=ListBuffer()
  for(elem <- source) buffer += f(elem)
  buffer.toIndexedSeq
}

val lifted1: (Seq[Double]) => Seq[Double] =map(f1)
val lifted2: (Seq[Double]) => Seq[Int] =map(f2)
val lifted3: (Seq[Int]) => Seq[String] =map(f3)


trait Functor[F[_]]{
  def map[A,B](f:A=>B)(sa:F[A]):F[B]
}

object ListFunctor extends Functor[List]{
  override def map[A, B](f: (A) => B)(sa: List[A]): List[B] = sa.map(f)
}

object OptionFunctor extends Functor[Option]{
  override def map[A, B](f: (A) => B)(sa: Option[A]): Option[B] = sa.map(f)
}