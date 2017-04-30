package com.eb.schedule.model


import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 20.02.2016.
  */
abstract class BasicFunSuiteTest extends FunSuite with BasicDBTest with ScalaFutures {
  private val log = LoggerFactory.getLogger(this.getClass)


  import org.scalatest.time.{Millis, Seconds, Span}

  implicit val defaultPatience = PatienceConfig(timeout = Span(10, Seconds), interval = Span(1000, Millis))
}
