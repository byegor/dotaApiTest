package com.eb.dotapulse.rest.http.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eb.dotapulse.bot.data.{Data, DataStorage, JsonSupport}
import com.eb.dotapulse.bot.http.routes.DataRoute
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by Egor on 15.04.2017.
  */
class DataRouteTest extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {
  val route: Route = new DataRoute().route

  "Put data call " should {
    "work with empty data" in {
      val data = Data("", Map(), Map())
      Put("/", data) ~> route ~> check {
        responseAs[String] shouldEqual "OK"
      }
    }

    "work wit data " in {
      Put("/", Data("currentGames", Map("321" ->"MATCH"), Map("123" ->"mathces"))) ~> route ~> check {
        responseAs[String] shouldEqual "OK"
        DataStorage.getCurrentGames shouldEqual "currentGames"
        DataStorage.getMatchById("321") shouldEqual "MATCH"
        DataStorage.getMatchesByGameId("123") shouldEqual "mathces"

      }
    }
  }



}
