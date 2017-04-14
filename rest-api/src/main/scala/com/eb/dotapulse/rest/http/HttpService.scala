package com.eb.dotapulse.rest.http

import akka.http.scaladsl.server.Directives._
import com.eb.dotapulse.rest.http.routes.{DataRoute, GamesRoute, MatchRoute}

/**
  * Created by Egor on 14.04.2017.
  */
class HttpService {

  private val matchRoute = new MatchRoute
  private val gameRoute = new GamesRoute
  private val dataRoute = new DataRoute


  private val v1Routes =
    pathPrefix("v1") {
      matchRoute.route ~
        gameRoute.route ~
        dataRoute.route
    }


  val route = v1Routes
}
