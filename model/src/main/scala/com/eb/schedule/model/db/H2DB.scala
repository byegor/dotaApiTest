package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Egor on 20.02.2016.
  */
object H2DB extends DB{
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("h2")
  def db = dbConfig.db
}
