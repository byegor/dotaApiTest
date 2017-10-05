package com.eb.pulse.crawler.data.model

import java.util

import com.eb.schedule.shared.bean.{GameBean, Match}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Created by Egor on 21.04.2017.
  */
object GameDataHolder {
  private val liveMatchesId = new util.HashSet[Long]()

  var gatheredData: Data = new Data(Map.empty, Map.empty, Map.empty)

  def setLiveMatchId(liveMatch: LiveMatch): Unit = {
    liveMatchesId.add(liveMatch.matchId)
  }

  def isLiveMatchExists(matchId: Long): Boolean = {
    liveMatchesId.contains(matchId)
  }

  def getLiveMatchesId(): mutable.Set[Long] = {
    asScalaSetConverter(liveMatchesId).asScala
  }

  def removeLiveMatch(matchId: Long): Boolean = {
    liveMatchesId.remove(matchId)
  }
}

//todo matches by games should contains gameid to list of mathces id
case class Data(currentGames: Map[String, List[GameBean]], currentMatches: Map[String, Match], matchesByGames: Map[String, Seq[String]])