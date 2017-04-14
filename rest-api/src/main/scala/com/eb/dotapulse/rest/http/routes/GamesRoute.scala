package com.eb.dotapulse.rest.http.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import com.eb.dotapulse.rest.data.DataStorage

/**
  * Created by Egor on 11.04.2017.
  */
class GamesRoute {

  val route = pathPrefix("games") {
    get {
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, DataStorage.getCurrentGames))
      } ~
        path(IntNumber) { gameId =>
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, DataStorage.getMatchesByGameId(gameId)))
        }
    }
  }
}
