package pl.pawelwlodarski.spark.functionalchain

import org.apache.spark.sql.DataFrame

import scala.collection.immutable.IndexedSeq


/**
 * Created by pawel on 20.09.15.
 */
object AbstractExample {


  val generalTimestampFunction: (DataFrame=>DataFrame) = {df =>
    import org.apache.spark.sql.functions._
    df.withColumn("created",current_date())
  }

  val businessOperation: DataFrame => DataFrame = { df =>
    df.select("column_with_business_meaning1","other_column1","other_column2","created")
  }

  val businessReport: DataFrame => DataFrame = { df =>
    import org.apache.spark.sql.functions._
    df.groupBy("created").agg(count("column_with_business_meaning1"))
  }

  val chain1 = generalTimestampFunction andThen businessOperation andThen businessReport



  val preconfiguredLogic : Long => DataFrame => DataFrame = businessKey =>  {inputFrame =>
    inputFrame.select("column1,column2,created").where(inputFrame("businessRelation")=== businessKey)
  }

  val CARS_INDUSTRY=1
  val MOVIE_INDUSTRY=1
  val chain2=generalTimestampFunction andThen preconfiguredLogic(CARS_INDUSTRY)
  val chain3=generalTimestampFunction andThen preconfiguredLogic(MOVIE_INDUSTRY)



  val businessJoin: (DataFrame,DataFrame) => DataFrame ={ (frame1,frame2) =>
    frame1.join(frame2,Seq("column_with_business_meaning1","column_with_business_meaning2"))
  }

  val businessUnion: (DataFrame,DataFrame) => DataFrame ={ (frame1,frame2) =>
    //some assertions that both frames are prepared for union
    frame1 unionAll frame2
  }

  val businessOperation2: DataFrame => DataFrame = { df =>
      import org.apache.spark.sql.functions._
      df.groupBy("column_with_business_meaning1").agg(max("expected_column1"),count("expected_column2"))
  }


  //Creating liftToTransformation mechanism
  type Datadictionary=Map[String,Any]
  type TransformationPhase=Datadictionary => Datadictionary


  def liftToTransformation1(f:DataFrame=>DataFrame)(key:String): TransformationPhase = { dictionary =>
    val frame =dictionary(key).asInstanceOf[DataFrame]
    val result=f(frame)
    //how to create a new dictionary???
    ???
  }


  type PhaseResult=(String,DataFrame)

  val primitiveFunctionAware: DataFrame => PhaseResult={df =>
    val result=df.select("important_column")
    ("KEY",result)
  }

  val primitiveFunctionCurried: String => DataFrame => PhaseResult=key => {df =>
    val result=df.select("important_column")
    (key,result)
  }

  val primitiveTwoParamFunction: String => (DataFrame,DataFrame) => PhaseResult=key => {(df1,df2) =>
    val result=df1 unionAll df2
    (key,result)
  }


  def liftToTransformation2(f:DataFrame=>PhaseResult)(key:String): TransformationPhase = { dictionary =>
    val frame =dictionary(key).asInstanceOf[DataFrame]
    dictionary + f(frame)
  }

  trait Extractor[A]{
    def extract(dictionary:Datadictionary)(key:String):A
  }

  implicit object DataFramExtractor extends Extractor[DataFrame] {
    override def extract(dictionary:Datadictionary)(key: String): DataFrame = dictionary(key).asInstanceOf[DataFrame]
  }

  implicit object LongExtractor extends Extractor[Long] {
    override def extract(dictionary:Datadictionary)(key: String): Long = dictionary(key).asInstanceOf[Long]
  }

  def liftToTransformation[A:Extractor](f:A=>PhaseResult)(key1:String): TransformationPhase = { dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    dictionary + f(param1)
  }

  def liftToTransformation[A:Extractor,B:Extractor](f:(A,B)=>PhaseResult)(key1:String,key2:String): TransformationPhase = { dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    val param2 =implicitly[Extractor[B]].extract(dictionary)(key2)
    dictionary + f(param1,param2)
  }

  def liftToTransformation[A:Extractor,B:Extractor,C:Extractor](f:(A,B,C)=>PhaseResult)(key1:String,key2:String,key3:String): TransformationPhase = { dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    val param2 =implicitly[Extractor[B]].extract(dictionary)(key2)
    val param3 =implicitly[Extractor[C]].extract(dictionary)(key3)
    dictionary + f(param1,param2,param3)
  }

  val functionLiftedToTransformation: TransformationPhase =liftToTransformation(primitiveFunctionCurried("RESULT_KEY"))("INPUT_FRAME")


  object Transformation{
    def init(loader:String => Any)(keys:String*) : Transformation = {
      val inititalDictionary=keys.map(key => (key,loader(key))).toMap
      new Transformation(inititalDictionary)
    }
  }

  class Transformation(val dictionary:Datadictionary){
    def transform(f:Datadictionary=>Datadictionary) = new Transformation(f(dictionary))
    def cache(keys:String*)={
      keys.foreach(key => dictionary(key).asInstanceOf[DataFrame].cache())
      this
    }
  }

  //improvements - logging

  type Log = String
  type LoggableTransformationPhase=Datadictionary => (Datadictionary,Log)

  def liftToTransformationWithLogging[A:Extractor](f:A=>PhaseResult)(key1:String): LoggableTransformationPhase= { dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    val result=f(param1)
    val log=s"transformed $param1 for $key1 into $result"

    (dictionary + result,log)
  }

  class LoggableTransformation(val dictionary:Datadictionary,val log:Seq[Log]=Seq.empty[Log]){
    def transform(f:Datadictionary=>(Datadictionary,Log),transformationTitle:String) = {
      val (nextDictionary,transformationLog: Log)=f(dictionary)
      val newLog: Seq[Log] = log :+ transformationTitle :+ transformationLog
      new LoggableTransformation(nextDictionary,newLog)
    }
  }

  class DataDictionaryWithStatistics(val dictionary:Map[String,Any]){
    private var statistics: Map[String,Int] = ??? //how many times each dataframe was requested

    def asDataFrame(key:String)=dictionary
      .get(key)
      .map(_.asInstanceOf[DataFrame])
      .getOrElse(throw new RuntimeException(s"$key is not a Dataframe"))
  }

  def main(args: Array[String]) {
      val domainLoader:String=>Any = ???
      val domainTransformation1: TransformationPhase =liftToTransformation(primitiveFunctionCurried("RESULT_KEY1"))("FRAME1")
      val domainTransformation2: TransformationPhase =liftToTransformation(primitiveFunctionCurried("RESULT_KEY2"))("FRAME2")
      val domainTransformation3: TransformationPhase =liftToTransformation(primitiveTwoParamFunction("FINAL_RESULT"))("RESULT_KEY","RESULT_KEY2")

      val result=Transformation
        .init(domainLoader)("FRAME1","FRAME2","CONFIG")
        .transform(domainTransformation1)
        .cache("RESULT_KEY2")
        .transform(domainTransformation2)
        .transform(domainTransformation3)



  }
}
