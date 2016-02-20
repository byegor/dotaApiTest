package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Egor on 20.02.2016.
  */
trait MysqlDB extends DB{
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("hikari")
  val db = dbConfig.db
}
