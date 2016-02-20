package com.eb.schedule.model.db

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
  * Created by Egor on 20.02.2016.
  */
trait H2DB extends DB{
  val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("h2")
  val db = dbConfig.db
}
