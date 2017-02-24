package pl.wlodar.akka.examples.lazycomm

object BusinessLogicSplit {

  //assumption - max length larger than single word
  def split(businessSeparator:String)(text:String,maxLength:Int):Iterable[String] ={
    def shortEnoughWithAdditionalSpace(e:String,elements:List[String]) =
      (elements.head+e).length < maxLength

    text.split(businessSeparator).foldLeft(List("")){(result,e) =>
      if(shortEnoughWithAdditionalSpace(e,result))
        joinHeadWithSpace(e,result)
      else
        e::result
    }.reverse
  }

  private def joinHeadWithSpace(e:String,elements:List[String]) = (elements.head+" "+e) :: elements.tail


}
