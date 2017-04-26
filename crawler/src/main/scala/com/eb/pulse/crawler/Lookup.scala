package com.eb.pulse.crawler

import com.eb.pulse.crawler.service._
import com.eb.schedule.dao.{ItemRepositoryImpl, NetWorthRepositoryImpl, SeriesRepositoryImpl}
import com.eb.schedule.model.dao.{LeagueRepositoryImpl, ScheduledGameRepositoryImpl}
import com.eb.schedule.model.db.DB
import com.eb.schedule.utils.HttpUtils
import slick.jdbc.JdbcBackend

/**
  * Created by Egor on 20.04.2017.
  */
trait Lookup extends DB {

  implicit val database: JdbcBackend#DatabaseDef = dbConfig.db

  private val netWorthRepository = new NetWorthRepositoryImpl
  val scheduledGameRepository = new ScheduledGameRepositoryImpl
  protected val seriesRepository = new SeriesRepositoryImpl
  protected val itemRepository = new ItemRepositoryImpl
  protected val leagueRepository = new LeagueRepositoryImpl

  val netWorthService = new NetworthService(netWorthRepository)
  val gameService = new GameService(scheduledGameRepository)
  val matchService = new MatchService(seriesRepository)
  val itemService = new ItemService(itemRepository)
  val leagueService = new LeagueService(leagueRepository)

  val httpUtils = new HttpUtils

}
