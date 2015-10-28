package pl.pawelwlodarski.fp.cats

/**
 * Created by pawel on 06.10.15.
 */
object CatsExperimentsMain {

  import simulacrum._
  @typeclass trait CanTruthy[A] { self =>
    /** Return true, if `a` is truthy. */
    def truthy(a: A): Boolean
  }
  object CanTruthy {
    def fromTruthy[A](f: A => Boolean): CanTruthy[A] = new CanTruthy[A] {
      def truthy(a: A): Boolean = f(a)
    }
  }


  def main(args: Array[String]) {
    implicit val intCanTruthy: CanTruthy[Int] = CanTruthy.fromTruthy({
      case 0 => false
      case _ => true
    })

  }
}
