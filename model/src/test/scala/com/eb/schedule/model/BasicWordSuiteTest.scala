package com.eb.schedule.model

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{AppendedClues, Matchers, WordSpec}

/**
  *
  */
abstract class BasicWordSuiteTest extends WordSpec with AppendedClues with Matchers with BasicDBTest with ScalaFutures {

  import org.scalatest.time.{Millis, Seconds, Span}

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
}
