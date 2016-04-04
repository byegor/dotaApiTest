package com.eb.schedule.cache

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.dto.{ItemDTO, LeagueDTO}
import com.eb.schedule.model.services.{LeagueService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
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

  private val lock: AnyRef = new Object()

  private val unknownLeague: LeagueDTO = new LeagueDTO(-1)

  private val leagueCache: scala.collection.concurrent.Map[Int, LeagueDTO] = new ConcurrentHashMap[Int, LeagueDTO]()

  def getLeague(id: Int): LeagueDTO = {
    val maybeLeagueDTO: Option[LeagueDTO] = leagueCache.get(id)
    if(maybeLeagueDTO.isDefined){
      maybeLeagueDTO.get
    }else{
      loadLeague(id)
    }
  }

  def loadLeague(id:Int): LeagueDTO = {
    lock.synchronized{
      val maybeLeagueDTO: Option[LeagueDTO] = leagueCache.get(id)
      if(maybeLeagueDTO.isDefined){
        maybeLeagueDTO.get
      }else{
        val result: Option[LeagueDTO] = Await.result(leagueService.findById(id), Duration.Inf)
        if(result.isDefined){
          leagueCache.put(id, result.get)
          result.get
        }else{
          taskService.insert(new UpdateTask(id, League.getClass.getSimpleName, 0))
          unknownLeague
        }
      }
    }
  }

}
