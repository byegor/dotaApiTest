package com.eb.schedule.cache

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.dto.{HeroDTO, LeagueDTO, TeamDTO}
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, Team, UpdateTask}
import com.eb.schedule.services.HeroService
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
  private val lock: AnyRef = new Object()

  val unknownTeam: TeamDTO = new TeamDTO(-1)

  private val cache: scala.collection.concurrent.Map[Int, TeamDTO] = new ConcurrentHashMap[Int, TeamDTO]()



  def getTeam(id: Int): TeamDTO = {
    val teamOpt: Option[TeamDTO] = cache.get(id)
    if(teamOpt.isDefined){
      teamOpt.get
    }else{
      loadTeam(id)
    }
  }

  def loadTeam(id:Int): TeamDTO = {
    lock.synchronized{
      val teamOpt: Option[TeamDTO] = cache.get(id)
      if(teamOpt.isDefined){
        teamOpt.get
      }else{
        val result: Option[TeamDTO] = Await.result(teamService.findById(id), Duration.Inf)
        if(result.isDefined){
          cache.put(id, result.get)
          result.get
        }else{
          taskService.insert(new UpdateTask(id, Team.getClass.getSimpleName, 0))
          unknownTeam
        }
      }
    }
  }


}
