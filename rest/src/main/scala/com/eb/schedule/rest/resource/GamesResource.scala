package com.eb.schedule.rest.resource

import java.util.concurrent.TimeUnit

import com.eb.schedule.services.ScheduleRestService
import com.eb.schedule.shared.bean.{GameBean, Match}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

/**
  * Created by Egor on 13.09.2015.
  */
class GamesResource(scheduleRestService: ScheduleRestService) extends ScalatraServlet {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  val currentGames: LoadingCache[Int, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(1, TimeUnit.MINUTES)
    .maximumSize(2)
    .build(new CacheLoader[Int, String]() {
      def load(ignore: Int): String = {
        val games: Map[String, Seq[GameBean]] = scheduleRestService.getGameByDate(new DateTime().getMillis)
        games.foreach(entry => entry._2.sortWith(_.gameStatus > _.gameStatus))
        mapper.writeValueAsString(games)
      }
    }).asInstanceOf[LoadingCache[Int, String]]

  val games: LoadingCache[Int, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(1, TimeUnit.MINUTES)
    .maximumSize(20)
    .build(new CacheLoader[Int, String]() {
      def load(gameId: Int): String = {
        val matches: Seq[Match] = scheduleRestService.getGameMatchesById(gameId)
        mapper.writeValueAsString(matches)
      }
    }).asInstanceOf[LoadingCache[Int, String]]

  val matches: LoadingCache[Long, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(1, TimeUnit.MINUTES)
    .maximumSize(20)
    .build(new CacheLoader[Long, String]() {
      def load(matchId: Long): String = {
        val m: Option[Match] = scheduleRestService.getMatchById(matchId)
        if(m.isDefined){
          mapper.writeValueAsString(m.get)
        }else{
          ""
        }
      }
    }).asInstanceOf[LoadingCache[Long, String]]


  get("/current") {
    currentGames.get(1)
  }

  get("/game/:id") {
    games.get({params("id")}.toInt)
  }

  get("/match/:id") {
    matches.get({params("id")}.toLong)
  }
}
