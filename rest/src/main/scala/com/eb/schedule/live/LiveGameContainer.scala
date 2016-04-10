package com.eb.schedule.live

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.dto.{BasicGameInfoDTO, CurrentGameDTO}

import scala.collection.JavaConversions._

/**
  * Created by Egor on 23.03.2016.
  */
object LiveGameContainer {


  private val currentLiveGames: scala.collection.concurrent.Map[Long, CurrentGameDTO] = new ConcurrentHashMap[Long, CurrentGameDTO]

  private val basicGamesInfo: scala.collection.concurrent.Map[Long, BasicGameInfoDTO] = new ConcurrentHashMap[Long, BasicGameInfoDTO]

  def updateLiveGame(currentGameDTO: CurrentGameDTO): Unit = {
    currentLiveGames.put(currentGameDTO.matchId, currentGameDTO)
    basicGamesInfo.put(currentGameDTO.matchId, currentGameDTO.basicInfo)
  }

  def getLiveGame(matchId: Long): Option[CurrentGameDTO] = {
    currentLiveGames.get(matchId)
  }

  def exists(matchId: Long): Boolean = {
    currentLiveGames.canEqual(matchId)
  }

  def getLiveMatchesId(): Iterable[Long] = {
    currentLiveGames.keys
  }

  def removeLiveGame(matchId: Long) = {
    currentLiveGames.remove(matchId)
    basicGamesInfo.remove(matchId)
  }

}
