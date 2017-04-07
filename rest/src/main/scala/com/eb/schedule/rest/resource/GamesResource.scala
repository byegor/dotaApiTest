package com.eb.schedule.rest.resource

import com.eb.schedule.data.DataStorage
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
//todo move to separate module based on akka
//todo cache results for 1 min on nginx side
class GamesResource extends ScalatraServlet {

  get("/current") {
    DataStorage.getCurrentGames
  }

  get("/game/:id") {
    DataStorage.getMatchesByGameId({
      params("id")
    })
  }

  get("/match/:id") {
    DataStorage.getMatchById({
      params("id")
    })
  }
}
