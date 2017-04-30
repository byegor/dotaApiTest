package com.eb.pulse.crawler

import com.eb.pulse.crawler.service._
import com.eb.schedule.dao.{ItemRepositoryImpl, NetWorthRepositoryImpl, SeriesRepositoryImpl}
import com.eb.schedule.model.dao.{LeagueRepositoryImpl, ScheduledGameRepositoryImpl, UpdateTaskRepositoryImpl}
import com.eb.schedule.model.db.MysqlDB
import com.eb.schedule.utils.HttpUtils
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

/** f
  * Created by Egor on 20.04.2017.
  */
object Lookup extends BasicLookup with MysqlDB{

}
