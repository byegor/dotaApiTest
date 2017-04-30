package com.eb.pulse.crawler

import com.eb.pulse.crawler.service._
import com.eb.schedule.dao.{ItemRepositoryImpl, NetWorthRepositoryImpl, SeriesRepositoryImpl}
import com.eb.schedule.model.dao.{LeagueRepositoryImpl, ScheduledGameRepositoryImpl, UpdateTaskRepositoryImpl}
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

  val netWorthService = new NetworthService(netWorthRepository)
  val gameService = new GameService(scheduledGameRepository)
  val matchService = new MatchService(seriesRepository)
  val itemService = new ItemService(itemRepository)
  val leagueService = new LeagueService(leagueRepository)
  val taskService = new TaskService(taskRepository)

  val httpUtils = new HttpUtils

}
