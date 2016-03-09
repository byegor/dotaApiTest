package com.eb.schedule.model.db

import slick.jdbc.JdbcBackend

/**
  * Created by Egor on 13.02.2016.
  */

trait DB {

  def db:JdbcBackend#DatabaseDef

}
