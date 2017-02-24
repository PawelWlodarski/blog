package pl.wlodar.akka.test

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

class StandardActorTest extends TestKit(ActorSystem("test")) with WordSpecLike with MustMatchers with StopSystemAfterAll

