package com.eb.pulse.crawler.httpserver


import akka.http.scaladsl.server.Directives
import com.eb.pulse.crawler.data.model.GameDataHolder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Created by Egor on 28.09.2017.
  */
class DataRoute extends Directives {

  val mapper: ObjectMapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val route = pathPrefix("data") {
    get {
      pathSingleSlash {
        complete(mapper.writeValueAsString(GameDataHolder.gatheredData))
      }
    }
  }
}
