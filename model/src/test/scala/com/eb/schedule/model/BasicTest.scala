package com.eb.schedule.model


import _root_.slick.dbio.Effect.Read
import _root_.slick.driver.H2Driver.api._
import _root_.slick.jdbc.meta.MTable
import _root_.slick.lifted.TableQuery
import _root_.slick.profile.BasicStreamingAction
import com.eb.schedule.configure.{CoreModule, H2Module}
import com.eb.schedule.model.db.{DB, H2DB}
import com.eb.schedule.model.services._
import com.eb.schedule.model.slick.{Hero, League, _}
import com.eb.schedule.services.{HeroService, NetWorthService, SeriesService}
import com.google.inject.Guice
import org.h2.jdbc.JdbcSQLException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 20.02.2016.
  */
abstract class BasicTest extends FunSuite with BeforeAndAfter with ScalaFutures {
  private val log = LoggerFactory.getLogger(this.getClass)
  val injector = Guice.createInjector(new H2Module, new CoreModule)
  val db = injector.getInstance(classOf[DB]).db

  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[HeroService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])
  val netWorthService = injector.getInstance(classOf[NetWorthService])
  val seriesService = injector.getInstance(classOf[SeriesService])

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

//  before {
//    try {
//      Await.result(db.run(DBIO.seq(
//        League.table += new League(4210, ""),
//        Team.table += new Team(36, "", "", -1),
//        Team.table += new Team(1838315, "", "", -1)
//      ).transactionally
//      ), Duration.Inf)
//    } catch {
//      case e: JdbcSQLException => println("ignore: " + e.getMessage)
//    }
//  }

  after {
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

  import org.scalatest.time.{Millis, Seconds, Span}

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))
}
