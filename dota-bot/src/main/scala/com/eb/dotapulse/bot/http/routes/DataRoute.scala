package com.eb.dotapulse.bot.http.routes


import akka.http.scaladsl.server.{Directives, Route}
import com.eb.dotapulse.bot.data.{Data, DataStorage, JsonSupport}

/**
  * Created by Egor on 14.04.2017.
  */
class DataRoute extends Directives with JsonSupport{

  val route: Route = put {
    entity(as[Data]) { data =>
      DataStorage.setData(data)
      complete("OK")
    }
  }
}
