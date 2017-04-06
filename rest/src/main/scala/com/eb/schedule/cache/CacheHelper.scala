package com.eb.schedule.cache

import com.eb.schedule.dto.{HeroDTO, ItemDTO, LeagueDTO}
import com.google.inject.{Inject, Singleton}
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 26.03.2016.
  */
@Singleton
class CacheHelper @Inject()(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val playerCache: PlayerCache, val teamCache: TeamCache, matchCache: MatchCache) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def getHero(id: Int): HeroDTO = {
    heroCache.getHero(id)
  }

  def getTeam(id: Int): CachedTeam = {
    teamCache.getTeam(id)
  }

  def getLeague(id: Int): LeagueDTO = {
    leagueCache.getLeague(id)
  }

  def getMatch(id: Long): Option[String] = {
    matchCache.getMatch(id)
  }

  def getItem(id: Int): ItemDTO = {
    itemCache.getItem(id)
  }


}
