package com.eb.schedule.live

import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.eb.schedule.MatchDTO
import com.eb.schedule.dto.{BasicGameInfoDTO, CurrentGameDTO}
import com.eb.schedule.exception.CacheItemNotFound
import com.google.common.cache.{Cache, CacheBuilder, CacheLoader, LoadingCache}
import com.google.gson.{JsonArray, JsonObject}

import scala.collection.JavaConversions._

/**
  * Created by Egor on 23.03.2016.
  */
object GameContainer {


  private val currentLiveGames: scala.collection.concurrent.Map[Long, CurrentGameDTO] = new ConcurrentHashMap[Long, CurrentGameDTO]

  private val basicGamesInfo: scala.collection.concurrent.Map[Long, BasicGameInfoDTO] = new ConcurrentHashMap[Long, BasicGameInfoDTO]

  private val matchCache: Cache[Long, MatchDTO] = CacheBuilder.newBuilder()
    .expireAfterAccess(4, TimeUnit.DAYS)
    .maximumSize(100)
    .build().asInstanceOf[Cache[Long, MatchDTO]]

  def putMatch(matchDTO: MatchDTO): Unit ={
    matchCache.put(matchDTO.matchId, matchDTO)
  }

  def getMatch(matchId:Long):Option[MatchDTO] ={
    Option(matchCache.getIfPresent(matchId))
  }

  def updateLiveGame(currentGameDTO: CurrentGameDTO): Unit = {
    currentLiveGames.put(currentGameDTO.matchId, currentGameDTO)
    basicGamesInfo.put(currentGameDTO.matchId, currentGameDTO.basicInfo)
  }

  def getLiveGame(matchId: Long): Option[CurrentGameDTO] = {
    currentLiveGames.get(matchId)
  }

  def exists(matchId: Long): Boolean = {
    currentLiveGames.contains(matchId)
  }

  def getLiveMatchesId(): Iterable[Long] = {
    currentLiveGames.keys
  }

  def getLiveMatches(): Iterable[CurrentGameDTO] = {
    currentLiveGames.values
  }

  def removeLiveGame(matchId: Long) = {
    currentLiveGames.remove(matchId)
    basicGamesInfo.remove(matchId)
  }

}
