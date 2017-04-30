package com.eb.schedule.model.db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

/**
  * Created by Egor on 13.02.2016.
  */

trait MysqlDB extends DB {


  override def dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("jdbc")
}
