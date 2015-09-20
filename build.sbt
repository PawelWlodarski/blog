
// General

organization := "pl.pawelwlodarski"

name := """blogcode"""

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions += "-deprecation"



libraryDependencies ++= Seq(
  "org.xerial.snappy" % "snappy-java" % "1.0.5",
  "org.apache.spark" %% "spark-sql" % "1.5.0"
)
// Testing

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"




