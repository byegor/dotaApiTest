package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Egor on 13.02.2016.
  */

trait DB {

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("jdbc")


}
