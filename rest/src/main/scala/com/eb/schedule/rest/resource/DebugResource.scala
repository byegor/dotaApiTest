package com.eb.schedule.rest.resource

import com.eb.schedule.config.RestLookup
import com.eb.schedule.dto.CurrentGameDTO
import com.eb.schedule.live.GameContainer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
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
}
