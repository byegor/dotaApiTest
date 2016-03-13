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

  val teams = new TableQuery(tag => new Teams(tag))
  val leagues = new TableQuery(tag => new Leagues(tag))
  val lives = new TableQuery(tag => new LiveGames(tag))
  val tasks = new TableQuery(tag => new UpdateTasks(tag))
  val picks = new TableQuery(tag => new Picks(tag))
  val scheduledGames = new TableQuery(tag => new ScheduledGames(tag))

  val injector = Guice.createInjector(new H2Module, new CoreModule)
  val db = injector.getInstance(classOf[DB]).db

  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[LeagueService])
  val liveGameService = injector.getInstance(classOf[LiveGameService])
  val pickService = injector.getInstance(classOf[PickService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])

  val initDb = {
    val tables = Await.result(db.run(MTable.getTables), 2.seconds).toList
    if(tables.isEmpty){
      Await.result(db.run(DBIO.seq(
        teams.schema.create,
        tasks.schema.create,
        leagues.schema.create,
        lives.schema.create,
        picks.schema.create,
        scheduledGames.schema.create
      ).transactionally
      ), Duration.Inf)
    }
  }

  after {
    Await.result(db.run(DBIO.seq(
      scheduledGames.delete,
      lives.delete,
      picks.delete,
      teams.delete,
      tasks.delete,
      leagues.delete
    ).transactionally
    ), Duration.Inf)
  }


}
