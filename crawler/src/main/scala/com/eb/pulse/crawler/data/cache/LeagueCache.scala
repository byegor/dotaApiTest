package com.eb.pulse.crawler.data.cache

import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.data.service.{LeagueService, TaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by Egor on 26.03.2016.
  */
class LeagueCache(val leagueService: LeagueService, taskService: TaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val cache: LoadingCache[Int, League] = CacheBuilder.newBuilder()
    .expireAfterAccess(24, TimeUnit.HOURS)
    .maximumSize(20)
    .build(new CacheLoader[Int, League]() {
      def load(leagueId: Int): League = {
        val result: Option[League] = Await.result(leagueService.findByLeagueId(leagueId), 5 seconds)
        result match {
          case Some(league) => league
          case None => throw new CacheItemNotFoundException
        }
      }
    }).asInstanceOf[LoadingCache[Int, League]]

  def getLeague(id: Int): League = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFoundException]) {
          taskService.insert(new UpdateTask(id, League.getClass.getSimpleName, 0))
        } else {
          log.error("couldn't get league from cache: " + id, e)
        }
        League(id, "")
    }
  }
}
