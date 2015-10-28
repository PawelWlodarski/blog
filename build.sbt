
// General

organization := "pl.pawelwlodarski"

name := """blogcode"""

version := "1.0.0-SNAPSHOT"

//scalaVersion := "2.10.4"
scalaVersion := "2.11.7"

scalacOptions += "-deprecation"

val macroParaside = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
val kindProjector = compilerPlugin("org.spire-math" %% "kind-projector" % "0.5.2")

libraryDependencies ++= Seq(
  "org.xerial.snappy" % "snappy-java" % "1.0.5",
  "org.apache.spark" %% "spark-sql" % "1.5.1",
  macroParaside
)
// Testing

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"

libraryDependencies += "org.spire-math" %% "cats" % "0.2.0"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.4"





