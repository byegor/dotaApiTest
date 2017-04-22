package com.eb.pulse.crawler.data

import java.util

import com.eb.pulse.crawler.model.LiveMatch

import scala.collection.JavaConverters._

/**
  * Created by Egor on 21.04.2017.
  */
object GameDataHolder {


  //  private val currentLiveMatches: ConcurrentHashMap[Long, LiveMatch] = new ConcurrentHashMap[Long, LiveMatch]
  //  private val liveMatchIdByScheduledGameId: ConcurrentHashMap[Int, Long] = new ConcurrentHashMap[Int, Long]

  private val liveMatchesId = new util.HashSet[Long]()

  def updateLiveMatch(gameId: Int, liveMatch: LiveMatch): Unit = {
    liveMatchesId.add(liveMatch.matchId)
    //    currentLiveMatches.put(liveMatch.matchId, liveMatch)
    //    liveMatchIdByScheduledGameId.put(gameId, liveMatch.matchId)
  }

  def updateLiveMatch(liveMatch: LiveMatch): Unit = {
    //    currentLiveMatches.put(liveMatch.matchId, liveMatch)
  }

  def isLiveMatchExists(matchId: Long) = {
    //    currentLiveMatches.contains(matchId)
    liveMatchesId.contains(matchId)
  }

  def getLiveMatchesId() = {
    asScalaSetConverter(liveMatchesId).asScala
  }

  def removeLiveMatch(matchId:Long) = {
    liveMatchesId.remove(matchId)
  }


}
