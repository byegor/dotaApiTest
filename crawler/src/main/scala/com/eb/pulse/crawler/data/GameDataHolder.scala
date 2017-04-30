package com.eb.pulse.crawler.data

import java.util

import com.eb.pulse.crawler.model.LiveMatch

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by Egor on 21.04.2017.
  */
object GameDataHolder {
  private val liveMatchesId = new util.HashSet[Long]()

  def setLiveMatchId(liveMatch: LiveMatch): Unit = {
    liveMatchesId.add(liveMatch.matchId)
  }

  def isLiveMatchExists(matchId: Long): Boolean = {
    liveMatchesId.contains(matchId)
  }

  def getLiveMatchesId(): mutable.Set[Long] = {
    asScalaSetConverter(liveMatchesId).asScala
  }

  def removeLiveMatch(matchId:Long): Boolean = {
    liveMatchesId.remove(matchId)
  }


}
