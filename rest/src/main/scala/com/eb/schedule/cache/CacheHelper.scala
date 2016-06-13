package com.eb.schedule.cache

import com.eb.schedule.dto.{HeroDTO, LeagueDTO, TeamDTO}
import com.eb.schedule.services.HeroService
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class CacheHelper @Inject()(val heroCache: HeroCache, val itemCache: ItemCache, val leagueCache: LeagueCache, val playerCache: PlayerCache, val teamCache: TeamCache) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def getHero(id:Int):HeroDTO={
    heroCache.getHero(id)
  }

  def getTeam(id:Int):TeamDTO = {
    teamCache.getTeam(id)
  }

  def getLeague(id:Int):LeagueDTO = {
    leagueCache.getLeague(id)
  }



}