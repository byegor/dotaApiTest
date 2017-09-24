package com.eb.dotapulse.bot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.eb.dotapulse.bot.http.HttpService
import com.eb.dotapulse.bot.telegram.DotaStatisticsBot
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt

/**
  * Created by Egor on 11.04.2017.
  */
object BotStarter extends App {

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("telegram-dota-bot")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  val httpService = new HttpService

  DotaStatisticsBot.run()

  Http().bindAndHandle(httpService.route, host, port)

}
