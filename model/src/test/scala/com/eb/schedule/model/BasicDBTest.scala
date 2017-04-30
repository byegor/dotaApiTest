package com.eb.schedule.model

import _root_.slick.jdbc.H2Profile.api._
import com.eb.schedule.model.db.H2
import com.eb.schedule.model.slick.{Hero, League, _}
import org.h2.jdbc.JdbcSQLException
import org.scalatest._
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
trait BasicDBTest extends Suite with BeforeAndAfterEach with H2{
  private val log = LoggerFactory.getLogger(this.getClass)

  val initDb = {
    try {
      Await.result(db.run(DBIO.seq(
        Team.table.schema.create,
        UpdateTask.table.schema.create,
        League.table.schema.create,
        Hero.table.schema.create,
        Item.table.schema.create,
        ScheduledGame.table.schema.create,
        NetWorth.table.schema.create,
        MatchSeries.table.schema.create
      ).transactionally
      ), Duration.Inf)
    } catch {
      case e: JdbcSQLException => println("hope already exists :)")
    }
  }

  override def beforeEach() {
    Await.result(db.run(DBIO.seq(
      NetWorth.table.delete,
      MatchSeries.table.delete,
      ScheduledGame.table.delete,
      Team.table.delete,
      UpdateTask.table.delete,
      League.table.delete,
      Hero.table.delete,
      Item.table.delete
    ).transactionally
    ), Duration.Inf)
  }

}
