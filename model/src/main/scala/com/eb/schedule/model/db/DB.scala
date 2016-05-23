package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend

/**
  * Created by Egor on 13.02.2016.
  */

trait DB {

  val dbConfig: DatabaseConfig[JdbcProfile]

  def db: JdbcBackend#DatabaseDef
}
