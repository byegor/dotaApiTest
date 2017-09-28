package com.eb.pulse.crawler

import com.eb.pulse.crawler.data.cache._
import com.eb.pulse.crawler.data.parser.FinishedMatchParser
import com.eb.pulse.crawler.data.service._
import com.eb.schedule.dao.{HeroRepositoryImpl, ItemRepositoryImpl, NetWorthRepositoryImpl, SeriesRepositoryImpl}
import com.eb.schedule.model.dao.{LeagueRepositoryImpl, ScheduledGameRepositoryImpl, TeamRepositoryImpl, UpdateTaskRepositoryImpl}
import com.eb.schedule.model.db.DB
import com.eb.schedule.utils.HttpUtils

/**
  * Created by Iegor.Bondarenko on 29.04.2017.
  */
abstract class BasicLookup extends DB{

  val netWorthRepository = new NetWorthRepositoryImpl
  val scheduledGameRepository = new ScheduledGameRepositoryImpl
  val seriesRepository = new SeriesRepositoryImpl
  val itemRepository = new ItemRepositoryImpl
  val leagueRepository = new LeagueRepositoryImpl
  val taskRepository = new UpdateTaskRepositoryImpl
  val heroRepository = new HeroRepositoryImpl
  val teamRepository = new TeamRepositoryImpl

  val netWorthService = new NetworthService(netWorthRepository)
  val gameService = new GameService(scheduledGameRepository)
  val matchService = new MatchService(seriesRepository)
  val itemService = new ItemService(itemRepository)
  val leagueService = new LeagueService(leagueRepository)
  val taskService = new TaskService(taskRepository)
  val heroService = new HeroService(heroRepository)
  val teamService = new TeamService(teamRepository)

  val httpUtils = new HttpUtils


  val heroCache = new HeroCache(heroService, taskService)
  val itemCache = new ItemCache(itemService)
  val teamCache = new TeamCache(teamService, taskService)
  val playerCache = new PlayerCache(httpUtils)
  val leagueCache = new LeagueCache(leagueService, taskService)
  val finishedMatchCache = new FinishedMatchCache(new FinishedMatchParser(netWorthService), httpUtils)

  val cacheHelper = new CacheHelper(heroCache, itemCache, leagueCache, playerCache, teamCache, finishedMatchCache)

}
