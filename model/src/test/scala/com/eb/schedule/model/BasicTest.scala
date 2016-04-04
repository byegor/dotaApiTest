package com.eb.schedule.model


import _root_.slick.dbio.Effect.Read
import _root_.slick.driver.H2Driver.api._
import _root_.slick.jdbc.meta.MTable
import _root_.slick.lifted.TableQuery
import _root_.slick.profile.BasicStreamingAction
import com.eb.schedule.configure.{CoreModule, H2Module}
import com.eb.schedule.model.db.{DB, H2DB}
import com.eb.schedule.model.services._
import com.eb.schedule.model.slick._
import com.eb.schedule.services.HeroService
import com.google.inject.Guice
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 20.02.2016.
  */
abstract class BasicTest extends FunSuite with BeforeAndAfter {

  val injector = Guice.createInjector(new H2Module, new CoreModule)
  val db = injector.getInstance(classOf[DB]).db

  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[HeroService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])

  val initDb = {
    val tables = Await.result(db.run(MTable.getTables), 2.seconds).toList
    if (tables.isEmpty) {
      Await.result(db.run(DBIO.seq(
        Team.table.schema.create,
        UpdateTask.table.schema.create,
        League.table.schema.create,
        ScheduledGame.table.schema.create,
        NetWorth.table.schema.create,
        MatchSeries.table.schema.create
      ).transactionally
      ), Duration.Inf)
    }
  }

  after {
    Await.result(db.run(DBIO.seq(
      NetWorth.table.delete,
      MatchSeries.table.delete,
      ScheduledGame.table.delete,
      Team.table.delete,
      UpdateTask.table.delete,
      League.table.delete
    ).transactionally
    ), Duration.Inf)
  }
}
