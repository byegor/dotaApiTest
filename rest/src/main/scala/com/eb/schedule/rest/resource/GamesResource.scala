package com.eb.schedule.rest.resource

import java.util

import com.eb.schedule.services.ScheduleRestService
import com.eb.schedule.shared.bean.{GameBean, Match}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.Gson
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

import scala.collection.JavaConversions._

/**
  * Created by Egor on 13.09.2015.
  */
class GamesResource(scheduleRestService: ScheduleRestService) extends ScalatraServlet {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  get("/current") {
    val games: Map[String, Seq[GameBean]] = scheduleRestService.getGameByDate(new DateTime().getMillis)
    games.foreach(entry => entry._2.sortWith(_.gameStatus > _.gameStatus))
    mapper.writeValueAsString(games)

  }

  get("/game/:id") {
    val matches: Seq[Match] = scheduleRestService.getGameMatchesById({params("id")}.toInt)
    mapper.writeValueAsString(matches)
  }

  get("/match/:id") {
    val m: Option[Match] = scheduleRestService.getMatchById({params("id")}.toLong)
    if(m.isDefined){
      mapper.writeValueAsString(m.get)
    }else{
      ""
    }
  }
}
