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
    //2016-05-15 17:50:11
    var games: List[GameBean] = Nil
//    scheduleRestService.getGameByDate(new DateTime().getMillis)
    if (games.isEmpty) {
      games = scheduleRestService.getGameByDate(new DateTime(2016, 5, 15, 1, 2).getMillis)
    }
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
