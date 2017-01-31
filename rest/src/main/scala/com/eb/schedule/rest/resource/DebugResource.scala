package com.eb.schedule.rest.resource

import com.eb.schedule.config.RestLookup
import com.eb.schedule.dto.CurrentGameDTO
import com.eb.schedule.live.GameContainer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
class DebugResource extends ScalatraServlet {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  get("/current") {
    val matches: Iterable[CurrentGameDTO] = GameContainer.getLiveMatches()
    mapper.writeValueAsString(matches)
  }

  get("/cache/player") {
    mapper.writeValueAsString(RestLookup.playerCache.cache.asMap())
  }

  get("/games/:cnt") {
    val days = {
      params("cnt")
    }.toInt
    val games = RestLookup.scheduleRestService.getGameByDate(new DateTime().minusDays(days).getMillis)
    games.foreach(entry => entry._2.sortWith(_.gameStatus > _.gameStatus))
    mapper.writeValueAsString(games)

  }
}
