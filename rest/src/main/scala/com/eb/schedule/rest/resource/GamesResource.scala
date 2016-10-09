package com.eb.schedule.rest.resource

import com.eb.schedule.services.ScheduleRestService
import com.eb.schedule.shared.bean.{GameBean, Match}
import com.google.gson.Gson
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
class GamesResource(scheduleRestService: ScheduleRestService) extends ScalatraServlet {

  val gson = new Gson()
  get("/current") {
    var games: List[GameBean] = scheduleRestService.getGameByDate(new DateTime().getMillis)
    gson.toJson(games.toArray)
  }

  get("/game/:id") {
    val matches: Seq[Match] = scheduleRestService.getGameMatchesById({params("id")}.toInt)
    gson.toJson(matches.toArray)
  }

  get("/match/:id") {
    val m: Option[Match] = scheduleRestService.getMatchById({params("id")}.toLong)
    if(m.isDefined){
      gson.toJson(m)
    }else{
      ""
    }
  }
}
