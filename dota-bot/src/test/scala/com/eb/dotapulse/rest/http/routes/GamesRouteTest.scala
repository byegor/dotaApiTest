package com.eb.dotapulse.rest.http.routes

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by Egor on 15.04.2017.
  */
class GamesRouteTest extends WordSpec with Matchers with ScalatestRouteTest {
  /*val route: Route = new GamesRoute().route todo

  "Get current games should return live games" in {
    DataStorage.setData(Data("current", Map(), Map()))
    Get("/games/") ~> route ~> check {
      responseAs[String] shouldEqual "current"
    }
  }


  "Get matches by gameId" in {
    DataStorage.setData(Data("current", Map(), Map("123" -> "seq of matches")))
    Get("/games/123") ~> route ~> check {
      responseAs[String] shouldEqual "seq of matches"
    }
  }*/


}
