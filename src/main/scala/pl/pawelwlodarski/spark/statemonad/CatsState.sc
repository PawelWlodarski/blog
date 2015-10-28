import cats.free.Trampoline
import cats.state._
import cats.std.all._

type Dictionary=Map[String,Any]
type Log=String

val dictionary:Dictionary=Map("key1"->1,"key2"->2,"key3"->"value3")

def transform(key:String,value:Any,currentLog:String=""): State[Dictionary,Log] = State[Dictionary,Log]{ dict =>
  val newDict:Dictionary=dict + (key -> value)
  (newDict,currentLog + s": added $key : $value")
}


val state=transform("key4",4.0)

val toRun: Trampoline[(Dictionary, Log)] = state.run(dictionary)
toRun.run

val state2=transform("key5",true)


val transformation=for{
  s1<-transform("key4",4.0,"")
  s2<-transform("key5",5.0,s1)
} yield s2

transformation.run(dictionary).run

state.flatMap(log=>transform("key5",true,log)).run(dictionary).run

