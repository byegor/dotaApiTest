package com.eb.pulse.crawler.httpserver


import akka.http.scaladsl.server.Directives
import com.eb.pulse.crawler.data.model.GameDataHolder

/**
  * Created by Egor on 28.09.2017.
  */
class DataRoute extends Directives with JsonSupport{

  val route = pathPrefix("data") {
    get {
      pathSingleSlash {
        complete(GameDataHolder.gatheredData)
      }
    }
  }
}
