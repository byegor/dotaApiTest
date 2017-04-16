package com.eb.dotapulse.rest.http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import com.eb.dotapulse.rest.data.DataStorage

/**
  * Created by Egor on 14.04.2017.
  */
class MatchRoute {

  val route = pathPrefix("matches") {
    get {
      path(Segment) { matchId =>
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, DataStorage.getMatchById(matchId)))
      }
    }
  }

}
