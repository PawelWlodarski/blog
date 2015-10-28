import cats._
import cats.std.all._
import cats.syntax.functor._
import cats.syntax.apply._
import cats.syntax.flatMap._

object Catnip {
  implicit class IdOp[A](val a: A)  {
    def some: Option[A] = Some(a)
  }
  def none[A]: Option[A] = None
}
import Catnip._

import cats.data.Kleisli.function

val f = function[Option,Int,Int] { (x: Int) => (x+1).some }
val g = function[Option,Int,Int] { (x: Int) => (x * 100).some }

4.some >>= (f compose g).run
4.some >>= (f andThen g).run

val l = f.lift[List]
List(1, 2, 3) >>= l.run


