package com.eb.schedule.model.db
import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

/**
  * Created by Iegor.Bondarenko on 28.04.2017.
  */
trait H2 extends DB{
  override def dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("jdbc", ConfigFactory.load("application-test.conf"))
}
