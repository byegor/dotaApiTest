package com.eb.schedule.rest.resource

import com.eb.schedule.live.GameContainer
import org.scalatra.ScalatraServlet

/**
 * Created by Egor on 13.09.2015.
 */
class LiveResource extends ScalatraServlet {

  get("/") {
    GameContainer.getLiveMatches()
  }
}
