import sbt.Keys._

val akkaVersion = "2.4.16"

//akka
val akka = "com.typesafe.akka" %% "akka-actor" % akkaVersion
val akkaSLF = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
val akkaTest = "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "it"
val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
val akkaStreamsTest = "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "it"

//functional
val cats = "org.typelevel" %% "cats" % "0.9.0"

//infa
val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"

//test
val scalacheck = "org.scalacheck" %% "scalacheck" % "1.13.4"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test,it"

val root = (project in file("."))
  .configs(IntegrationTest)
    .settings(Defaults.itSettings: _*)
  .settings(
    organization := "pl.pawelwlodarski",
    name := """blogcode""",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.12.1",
    scalacOptions += "-deprecation"
  )
  .settings(
    libraryDependencies ++= Seq(
      akka, akkaSLF, akkaTest, akkaStream, akkaStreamsTest,
      scalacheck, scalatest,
      logback
    )
  )








