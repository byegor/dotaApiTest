package com.eb.schedule.cache

import java.util.concurrent.TimeUnit

import com.eb.schedule.dto.TeamDTO
import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class TeamCache @Inject()(val teamService: TeamService, taskService: UpdateTaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val cache: LoadingCache[Int, CachedTeam] = CacheBuilder.newBuilder()
    .expireAfterAccess(3, TimeUnit.HOURS)
    .maximumSize(200)
    .build(new CacheLoader[Int, CachedTeam]() {
      def load(teamId: Int): CachedTeam = {
        val result: Option[TeamDTO] = Await.result(teamService.findById(teamId), Duration.Inf)
        if (result.isDefined) {
          val teamDTO: TeamDTO = result.get
          CachedTeam(teamDTO.id, teamDTO.name, teamDTO.tag, teamDTO.logo)
        } else {
          throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Int, CachedTeam]]

  def getTeam(id: Int): CachedTeam = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFound]) {
          taskService.insert(new UpdateTask(id, Team.getClass.getSimpleName, 0))
        } else {
          log.error("couldn't get item from cache: ", e)
        }
        CachedTeam(id, "", "", -1)
    }
  }

  def invalidateTeam(id:Int): Unit ={
    cache.invalidate(id)
  }


}
