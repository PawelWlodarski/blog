trait RNG{
  def nextInt : (Int,RNG)
}

case class SimpleRNG(seed: Long) extends RNG {
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL // `&` is bitwise AND. We use the current seed to generate a new seed.
    val nextRNG = SimpleRNG(newSeed) // The next state, which is an `RNG` instance created from the new seed.
    val n = (newSeed >>> 16).toInt // `>>>` is right binary shift with zero fill. The value `n` is our new pseudo-random integer.
    (n, nextRNG) // The return value is a tuple containing both a pseudo-random integer and the next `RNG` state.
  }
}

def nonNegativeInt(rng: RNG): (Int, RNG) = {
  val (i, r) = rng.nextInt
  (if (i < 0) -(i + 1) else i, r)
}


val gen=SimpleRNG(100)


type Rand[+A] = RNG => (A, RNG)


val int: Rand[Int] = _.nextInt
val double: Rand[Double] = map(nonNegativeInt)(_ / (Int.MaxValue.toDouble + 1))


def unit[A](a:A) : Rand[A] = rng => (a,rng)

def map[A,B](s:Rand[A])(f:A=>B) : Rand[B] =
  rng =>{
    val (a,rng2)=s(rng)
    (f(a),rng2)
  }

def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = rng =>{
  val (a,rng1) = ra(rng)
  val (b,rng2) = rb(rng1)
  (f(a,b),rng2)
}

def both[A,B](ra: Rand[A], rb: Rand[B]): Rand[(A,B)] = map2(ra, rb)((_, _))

val randIntDouble: Rand[(Int, Double)] = both(int, double)
val randDoubleInt: Rand[(Double, Int)] = both(double, int)

def nonNegativeEven: Rand[Int] = map(nonNegativeInt)(i => i - 1 % 2)

def sequence[A](fs: List[Rand[A]]): Rand[List[A]] =
  fs.foldRight(unit(List[A]()))((f,acc)=>map2(f,acc)(_ :: _))


def _ints(count: Int): Rand[List[Int]] = sequence(List.fill(count)(int))

def flatMap[A,B](f:Rand[A])(g: A => Rand[B]) : Rand[B] = rand => {
  val (a,rng1) = f(rand)
  g(a)(rng1)
}

def map_f[A,B](s:Rand[A])(f:A=>B) : Rand[B] = flatMap(s)(a=>unit(f(a)))
def map2_f[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] =
  flatMap(ra)(a=> map(rb)(b=>f(a,b)))

def maps[S,A,B](a: S => (A,S))(f: A => B): S => (B,S) = ???

case class State[S,+A](run: S => (A,S))




