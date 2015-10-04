package pl.pawelwlodarski.spark.functionalchain

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, DataFrame}
import org.apache.spark.sql.functions._

/**
 * Created by pawel on 04.10.15.
 */
object ChainOnstateMonad {

  import scalaz.State

  type DataDictionary=Map[String,Any]
  type Log=String

  val addTimeStamp: DataFrame => DataFrame = { df =>
    df.withColumn("created",current_date())
  }

  val addLabel:String => DataFrame => DataFrame = {label => df =>
    df.withColumn("label",lit(label))
  }

  val businessJoin : (String,String) => (DataFrame,DataFrame) => DataFrame = {(column1,column2) => (df1,df2) =>
      df1.join(df2, df1(column1) === df2(column2))
  }

  //lift
  type TransformationPhase=DataDictionary => (DataDictionary,Log)

  trait Extractor[A]{
    def extract(dictionary:DataDictionary)(key:String):A
  }

  implicit object DataFramExtractor extends Extractor[DataFrame] {
    override def extract(dictionary:DataDictionary)(key: String): DataFrame = dictionary(key).asInstanceOf[DataFrame]
  }


  def liftToTransformation[A:Extractor](f:A=>DataFrame)(key1:String)(resultKey:String):String => TransformationPhase = {currentLog => dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    val result=f(param1)
    val log=currentLog + s"\nadding $resultKey -> $result"
    val newDictionary=dictionary + (resultKey -> result)
    (newDictionary,log)
  }

  def liftToTransformation[A:Extractor,B:Extractor](f:(A,B)=>DataFrame)(key1:String,key2:String)(resultKey:String): String => TransformationPhase = {currentLog => dictionary =>
    val param1 =implicitly[Extractor[A]].extract(dictionary)(key1)
    val param2 =implicitly[Extractor[B]].extract(dictionary)(key2)
    val result=f(param1,param2)
    val log=currentLog + s"\nadding $resultKey -> $result"
    val newDictionary=dictionary + (resultKey -> result)
    (newDictionary,log)
  }

  val addTimestampPhase=liftToTransformation(addTimeStamp)("InitialFrame")("WithTimeStamp")
  val addLabelPhase=liftToTransformation(addLabel("experiment"))("WithTimeStamp")("Labelled")
  val businessJoinPhase=liftToTransformation(businessJoin("customerId","id"))("Labelled","SomeOtherFrame")("JoinedByBusinessRules")

  val transformation1:Log => State[DataDictionary,Log] =initialLog => for{
    log1 <- State(addTimestampPhase(initialLog))
    log2 <- State(addLabelPhase(log1))
    log3 <- State(businessJoinPhase(log2))
  } yield log3

  //transformation2
  val importantSelect:DataFrame => DataFrame = _.select("customerId","credit","label","created")
  val importantSelectPhase =liftToTransformation(importantSelect)("JoinedByBusinessRules")("BusinessReport")

  val transformation2:Log => State[DataDictionary,Log]=initialLog => State(importantSelectPhase(initialLog))

  val transformationComposed:Log => State[DataDictionary,Log]=initialLog=>for{
    logT1 <- transformation1(initialLog)
    logT2 <- transformation2(logT1)
  } yield logT2




  def main(args: Array[String]) {
    val config=new SparkConf().setMaster("local[4]").setAppName("Dataframes transformation with State Monad")
    val sc=new SparkContext(config)
    val sqlContext=new SQLContext(sc)
    import sqlContext.implicits._

    println("example start")

    val df1=sc.parallelize(Seq(
      (1,"cust1@gmail.com","Stefan"),
      (2,"cust2@gmail.com","Zdzislawa"),
      (3,"cust3@gmail.com","Bonifacy"),
      (4,"cust4@gmail.com","Bozebozebozenka")
    )).toDF("customerId","email","name")


    val df2=sc.parallelize(Seq(
      (1,10),
      (2,20),
      (3,30),
      (4,40)
    )).toDF("id","credit")

    val dictionary:DataDictionary=Map("InitialFrame" -> df1,"SomeOtherFrame"->df2)

    val (resultDictionary,log)=transformationComposed("").run(dictionary)
    println("**************LOG*************** : "+log)
    println("**************DICTIONARY********")
    resultDictionary.foreach(println)
    val result=resultDictionary("BusinessReport").asInstanceOf[DataFrame]
    result.show()
  }


}
