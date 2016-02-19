package com.eb.schedule.model.dao

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Egor on 13.02.2016.
  */

trait DBConf {

  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("hikari")
  val db = dbConfig.db
  import dbConfig.driver.api._
}
