package com.eb.pulse.crawler

import com.eb.schedule.model.db.H2
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}
import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}

import scala.io.BufferedSource

/**
  * Created by Iegor.Bondarenko on 29.04.2017.
  */
object TestLookup extends BasicLookup with H2 {

}
