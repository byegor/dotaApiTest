package com.eb.pulse.crawler

import com.eb.pulse.crawler.service.{GameService, MatchService, NetworthService}
import com.eb.schedule.dao.{NetWorthRepositoryImpl, SeriesRepositoryImpl}
import com.eb.schedule.model.dao.ScheduledGameRepositoryImpl
import com.eb.schedule.model.db.DB
import com.eb.schedule.utils.HttpUtils
import slick.jdbc.JdbcBackend

/**
  * Created by Egor on 20.04.2017.
  */
trait Lookup extends DB {

  implicit val db: JdbcBackend#DatabaseDef = dbConfig.db

  private val netWorthRepository = new NetWorthRepositoryImpl
  val scheduledGameRepository = new ScheduledGameRepositoryImpl
  protected val seriesRepository = new SeriesRepositoryImpl

  val netWorthService = new NetworthService(netWorthRepository)
  val gameService = new GameService(scheduledGameRepository)
  val matchService = new MatchService(seriesRepository)

  val httpUtils = new HttpUtils

}
