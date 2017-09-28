package com.eb.pulse.crawler.httpserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

/**
  * Created by Egor on 11.04.2017.
  */
object HtppServer {

  def start {

    val config = ConfigFactory.load()
    val host = config.getString("http.host")
    val port = config.getInt("http.port")

    implicit val system = ActorSystem("dotapulse-rest-api")
    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher
    implicit val timeout = Timeout(10 seconds)

    val dataRoute = new DataRoute


    Http().bindAndHandle(dataRoute.route, host, port)
  }
}
