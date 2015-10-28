

import scala.collection.mutable.ListBuffer

//Biblioteka miedzy projektowa
def map[A,B](f:A=>B)(source:Seq[A]): Seq[B] = {
  val buffer:scala.collection.mutable.ListBuffer[B]=ListBuffer()
  for(elem <- source) buffer += f(elem)
  buffer.toIndexedSeq
}

def reduce[A](zero:A)(operator:(A,A)=>A)(seq:Seq[A]):A={
  var suma=zero
  for{
    elem <- seq
  } suma=operator(suma,elem)
  suma
}

//to w sumie tez specjalistyczna biblioteka miedzyprojektowa
def addDouble=reduce(0.0)((a,b)=>a+b)_

//biblioteak domenowa
val GESTOSC_ALKOHOLU_ETYLOWEGO=0.79
val POJEMNOSC_BUTELKI_ML=500


case class Browar(marka:String,procenty:Double)
case class UczestnikZabawy(imie:String,waga:Int,browary:IndexedSeq[Browar])
def ileGram(b:Browar): Double =POJEMNOSC_BUTELKI_ML/100*b.procenty*GESTOSC_ALKOHOLU_ETYLOWEGO
val promile: (Double,Int)=>Double= (gramy,waga) => gramy / (waga * 0.7)



//pokaz
val miloslaw=Browar("Miloslaw" ,5.4)
val raciborskie=Browar("Raciborskie",4.8)
val piwoNaMiodzie=Browar("Piwo na miodzie",5.2)


val Stefan=UczestnikZabawy("Stefan", 100,
  IndexedSeq(miloslaw,miloslaw,raciborskie,piwoNaMiodzie,raciborskie))

val gramy=map(ileGram)(Stefan.browary)
val sumaGramow=addDouble(gramy)
promile(sumaGramow,Stefan.waga)

//lepiej ale nadal slabo - currying

val curried: (Seq[Browar]) => Double =map(ileGram)_ andThen addDouble
val promileCurried: Double =>Int=>Double= waga => gramy =>  gramy / (waga * 0.7)

val sumaGramow2=curried(Stefan.browary)
promileCurried(sumaGramow2)(Stefan.waga)


val getBrowary : UczestnikZabawy => Seq[Browar] = uz => uz.browary
//val getBrowary : UczestnikZabawy => Seq[Browar] = _.browary
val getWaga : UczestnikZabawy => Int = _.waga

val liczSumeGramow: (UczestnikZabawy) => Double =getBrowary andThen map(ileGram) andThen addDouble
val liczPromile: (Double) => (UczestnikZabawy) => (Double) =gramy => getWaga andThen promileCurried(gramy)

val sumaGramow3=liczSumeGramow(Stefan)
liczPromile(sumaGramow3)(Stefan)
//res2: Double = 1.4127486437613022


import scalaz._
import Scalaz._

val maaaaaagic: (UczestnikZabawy) => Double =liczSumeGramow flatMap liczPromile
maaaaaagic(Stefan)
//res3: Double = 1.4127486437613022





