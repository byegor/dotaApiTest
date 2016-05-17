package com.eb.schedule.cache

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.eb.schedule.dto.{HeroDTO, LeagueDTO, TeamDTO}
import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, Team, UpdateTask}
import com.eb.schedule.services.HeroService
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.JavaConversions._

/**
  * Created by Egor on 26.03.2016.
  */
class TeamCache @Inject()(val teamService: TeamService, taskService: UpdateTaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val cache: LoadingCache[Int, TeamDTO] =  CacheBuilder.newBuilder()
    .expireAfterAccess(3, TimeUnit.HOURS)
    .maximumSize(200)
    .build(new CacheLoader[Int, TeamDTO]() {
      def load(teamId: Int): TeamDTO = {
        val result: Option[TeamDTO] = Await.result(teamService.findById(teamId), Duration.Inf)
        if (result.isDefined) {
          result.get
        } else {
          throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Int, TeamDTO]]

  def getTeam(id: Int): TeamDTO = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        if(e.getCause.isInstanceOf[CacheItemNotFound]){
          taskService.insert(new UpdateTask(id, Team.getClass.getSimpleName, 0))
        }else{
          log.error("couldn't get item from cache: ", e)
        }
        new TeamDTO(id)
    }
  }


}
