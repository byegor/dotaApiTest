package com.eb.schedule.cache

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.eb.schedule.dto.{ItemDTO, LeagueDTO, TeamDTO}
import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.model.services.{LeagueService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class LeagueCache @Inject()(val leagueService: LeagueService, taskService: UpdateTaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val cache: LoadingCache[Int, LeagueDTO] = CacheBuilder.newBuilder()
    .expireAfterAccess(3, TimeUnit.HOURS)
    .maximumSize(10)
    .build(new CacheLoader[Int, LeagueDTO]() {
      def load(leagueId: Int): LeagueDTO = {
        val result: Option[LeagueDTO] = Await.result(leagueService.findById(leagueId), Duration.Inf)
        if (result.isDefined) {
          result.get
        } else {
          throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Int, LeagueDTO]]

  def getLeague(id: Int): LeagueDTO = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        taskService.insert(new UpdateTask(id, League.getClass.getSimpleName, 0))
        log.debug("Couldn't find league: " + id)
        new LeagueDTO(id)
    }
  }
}
