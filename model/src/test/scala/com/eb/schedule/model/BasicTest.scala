package com.eb.schedule.model


import _root_.slick.driver.H2Driver.api._
import _root_.slick.lifted.TableQuery
import com.eb.schedule.model.db.H2DB
import com.eb.schedule.model.slick.{Leagues, LiveGames, Teams}
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
abstract class BasicTest extends FunSuite with H2DB with BeforeAndAfter {

  val teams = new TableQuery(tag => new Teams(tag))
  val leagues = new TableQuery(tag => new Leagues(tag))
  val lives = new TableQuery(tag => new LiveGames(tag))

  before {
   Await.result( db.run(DBIO.seq(
      teams.schema.create,
      leagues.schema.create,
      lives.schema.create
    ).transactionally
    ), Duration.Inf)
  }


}
