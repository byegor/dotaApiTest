package egor.dota.ui.resource

import org.scalatra.ScalatraServlet

/**
 * Created by Egor on 13.09.2015.
 */
class TeamResource extends ScalatraServlet {

  get("/") {
    "Hi there!"
  }
}
