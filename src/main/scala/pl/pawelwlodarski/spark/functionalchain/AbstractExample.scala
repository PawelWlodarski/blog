package pl.pawelwlodarski.spark.functionalchain

import org.apache.spark.sql.DataFrame


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

  val businessOperation2: DataFrame => DataFrame = { df =>
      import org.apache.spark.sql.functions._
      df.groupBy("column_with_business_meaning1").agg(max("expected_column1"),count("expected_column2"))
  }




}
