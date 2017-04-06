package com.eb.schedule.rest.resource

import com.eb.schedule.data.DataStorage
import com.eb.schedule.services.ScheduleRestService
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
class GamesResource(scheduleRestService: ScheduleRestService) extends ScalatraServlet {

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
