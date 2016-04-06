package com.eb.schedule

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.dto.{BasicGameInfoDTO, CurrentGameDTO, ScheduledGameDTO}

import scala.collection.JavaConversions._

/**
  * Created by Egor on 23.03.2016.
  */
object LiveGameContainer {


  private val currentLiveGames: scala.collection.concurrent.Map[Long, CurrentGameDTO] = new ConcurrentHashMap[Long, CurrentGameDTO]

  private val scheduledGames: scala.collection.concurrent.Map[Long, BasicGameInfoDTO] = new ConcurrentHashMap[Long, BasicGameInfoDTO]

  def updateLiveGame(currentGameDTO: CurrentGameDTO): Unit = {
    currentLiveGames.put(currentGameDTO.matchId, currentGameDTO)
    scheduledGames.put(currentGameDTO.matchId, currentGameDTO.basicInfo)
  }

  def getLiveMatchesId():Iterable[Long] = {
    currentLiveGames.keys
  }

}
