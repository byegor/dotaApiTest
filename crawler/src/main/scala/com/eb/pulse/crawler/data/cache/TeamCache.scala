package com.eb.pulse.crawler.data.cache

import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.data.service.{TaskService, TeamService}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
  * Created by Egor on 26.03.2016.
  */
class TeamCache (val teamService: TeamService, taskService: TaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val cache: LoadingCache[Int, Team] = CacheBuilder.newBuilder()
    .expireAfterWrite(20, TimeUnit.HOURS)
    .maximumSize(200)
    .build(new CacheLoader[Int, Team]() {
      def load(teamId: Int): Team = {
        val result: Option[Team] = Await.result(teamService.findByTeamId(teamId), 5 seconds)
        result match {
          case Some(team) => team
          case None => throw new CacheItemNotFoundException
        }
      }
    }).asInstanceOf[LoadingCache[Int, Team]]

  def getTeam(id: Int): Team = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFoundException]) {
          taskService.insert(new UpdateTask(id, Team.getClass.getSimpleName, 0))
        } else {
          log.error("couldn't get item from cache for id: " + id, e)
        }
        Team(id)
    }
  }

  def put(team:Team): Unit ={
    cache.put(team.id, team)
  }


}
