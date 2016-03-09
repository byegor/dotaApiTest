package com.eb.schedule.model


import _root_.slick.driver.H2Driver.api._
import _root_.slick.lifted.TableQuery
import com.eb.schedule.configure.{CoreModule, H2Module}
import com.eb.schedule.model.db.{DB, H2DB}
import com.eb.schedule.model.services.{UpdateTaskServiceImpl, UpdateTaskService, TeamService}
import com.eb.schedule.model.slick.{UpdateTasks, Leagues, LiveGames, Teams}
import com.google.inject.Guice
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
abstract class BasicTest extends FunSuite with BeforeAndAfter {

  val teams = new TableQuery(tag => new Teams(tag))
  val leagues = new TableQuery(tag => new Leagues(tag))
  val lives = new TableQuery(tag => new LiveGames(tag))
  val tasks = new TableQuery(tag => new UpdateTasks(tag))

  val injector = Guice.createInjector(new H2Module, new CoreModule)
  val db = injector.getInstance(classOf[DB]).db

  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskServiceImpl])

  before {
   Await.result( db.run(DBIO.seq(
      teams.schema.create,
      tasks.schema.create,
      leagues.schema.create,
      lives.schema.create
    ).transactionally
    ), Duration.Inf)
  }




}
