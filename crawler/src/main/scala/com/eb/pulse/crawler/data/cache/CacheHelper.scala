package com.eb.pulse.crawler.data.cache

import com.eb.schedule.model.slick.{Hero, Item, League, Team}
import com.eb.schedule.shared.bean.Match
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 26.03.2016.
  */
class CacheHelper(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val playerCache: PlayerCache, val teamCache: TeamCache, matchCache: FinishedMatchCache) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def getHero(id: Int): Hero = {
    heroCache.getHero(id)
  }

  def getTeam(id: Int): Team = {
    teamCache.getTeam(id)
  }

  def putTeam(team: Team): Unit ={
    teamCache.put(team)
  }

  def getLeague(id: Int): League = {
    leagueCache.getLeague(id)
  }

  def getMatch(id: Long): Option[Match] = {
    matchCache.getMatch(id)
  }

  def getItem(id: Int): Item = {
    itemCache.getItem(id)
  }

  def getPlayerName(accId: Int): String = {
    playerCache.getPlayerName(accId)
  }

  def putPlayerNames(playerNames: Map[Int, String]): Unit = {
    playerCache.put(playerNames)
  }

}
