def chainStops[S,A](
   c1: S => (S,A),
   c2: S => (S,A)
 ) : S => (S,A) = { s =>
  val (c1,_) = c1(s)
  c2(c1)
}

trait State[S, +A]{
  def run(initial: S) :  (S,A)
  def map[B](f: A=>B) : State[S,B] = ???
  def flatMap[B](f: A=>State[S,B]) : State[S,B] = ???
}

object State{
  def apply[S,A](f: S => (S,A)): State[S,A] = ???
}


