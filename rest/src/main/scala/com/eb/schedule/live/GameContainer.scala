package com.eb.schedule.live

import java.util.concurrent.ConcurrentHashMap

import com.eb.schedule.dto.CurrentGameDTO

import scala.collection.JavaConversions._

/**
  * Created by Egor on 23.03.2016.
  */
object GameContainer {


  private val currentLiveMatches: scala.collection.concurrent.Map[Long, CurrentGameDTO] = new ConcurrentHashMap[Long, CurrentGameDTO]

  private val liveMatchIdByScheduledGameId: scala.collection.concurrent.Map[Int, Long] = new ConcurrentHashMap[Int, Long]


  def addGameAndMapping(currentGameDTO: CurrentGameDTO): Unit = {
    currentLiveMatches.put(currentGameDTO.matchId, currentGameDTO)
    liveMatchIdByScheduledGameId.put(currentGameDTO.scheduledGameId, currentGameDTO.matchId)
  }

  def updateJustCurrentGame(currentGameDTO: CurrentGameDTO): Unit = {
    currentLiveMatches.put(currentGameDTO.matchId, currentGameDTO)
  }

  def getLiveGame(matchId: Long): Option[CurrentGameDTO] = {
    currentLiveMatches.get(matchId)
  }

  def exists(matchId: Long): Boolean = {
    currentLiveMatches.contains(matchId)
  }

  def getLiveMatchesId(): Iterable[Long] = {
    currentLiveMatches.keys
  }

  def getLiveMatches(): Iterable[CurrentGameDTO] = {
    currentLiveMatches.values
  }

  def removeLiveGame(matchId: Long, scheduledGameId: Int = 1) = {
    currentLiveMatches.remove(matchId)
    val orElse: Long = liveMatchIdByScheduledGameId.getOrElse(scheduledGameId, -1)
    if (orElse == matchId) {
      liveMatchIdByScheduledGameId.remove(scheduledGameId)
    }

  }

  def getLiveMatchIdByScheduledGameId(scheduledGameId: Int): Option[Long] = {
    liveMatchIdByScheduledGameId.get(scheduledGameId)
  }

}
