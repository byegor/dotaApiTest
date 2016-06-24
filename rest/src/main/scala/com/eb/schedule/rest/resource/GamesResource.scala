package com.eb.schedule.rest.resource

import java.time.temporal.ChronoField
import java.time.{LocalDate, LocalDateTime, ZoneId}

import com.eb.schedule.live.GameContainer
import com.eb.schedule.services.ScheduleRestService
import com.eb.schedule.shared.bean.GameBean
import com.google.gson.Gson
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
class GamesResource(scheduleRestService: ScheduleRestService) extends ScalatraServlet {

  val gson = new Gson()
  get("/current") {
    //2016-05-15 17:50:11
    var games: List[GameBean] = scheduleRestService.getGameByDate(new DateTime().getMillis)
    if (games.isEmpty) {
      games = scheduleRestService.getGameByDate(new DateTime(2016, 5, 15, 1, 2).getMillis)
    }
    gson.toJson(games.toArray)
    //    val games: List[GameBean] = scheduleRestService.getGameByDate(System.currentTimeMillis())

  }
}
