package com.eb.schedule.model

import org.scalatest.{FunSuite, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.slf4j.LoggerFactory

/**
  *
  */
abstract class BasicWordSuiteTest extends WordSpec with BasicDBTest with ScalaFutures {

  import org.scalatest.time.{Millis, Seconds, Span}

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
}
