import scalaz.State

type Dictionary=Map[String,Any]
type Log=String


def pureTransformation(key: String, value: Any, currentLog: Log): (Dictionary) => (Dictionary, Log) = {
  dict =>
    val newDict: Dictionary = dict + (key -> value)
    (newDict, currentLog + s": added $key : $value")
}
def stateTransform(key:String,value:Any,currentLog:Log="") = State[Dictionary,Log]{
    pureTransformation(key, value, currentLog)
}

val state=stateTransform("key4",4.0)

val initialDictionary:Dictionary=Map()
state.run(initialDictionary)

//nie ma trampoliny
state.run(dictionary)

val transformation : State[Dictionary,Log]=for{
  log1<-stateTransform("key4",4.0)
  log2<-stateTransform("key5",true,log1)
  finalLog<-stateTransform("key6","someValue",log2)
} yield finalLog



val dictionary:Dictionary=Map("key1"->1,"key2"->2,"key3"->"value3")
val (finalDictionary,log)=transformation.run(dictionary)
finalDictionary
log