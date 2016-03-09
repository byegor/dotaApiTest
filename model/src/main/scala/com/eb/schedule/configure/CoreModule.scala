package com.eb.schedule.configure

import com.eb.schedule.model.dao._
import com.eb.schedule.model.db.{DB, H2DB, MysqlDB}
import com.eb.schedule.model.services._
import com.google.inject.AbstractModule

/**
  * Created by Egor on 09.03.2016.
  */
class CoreModule extends AbstractModule{
  override def configure(): Unit = {
    //repository
    bind(classOf[TeamRepository]).to(classOf[TeamRepositoryImpl])
    bind(classOf[UpdateTaskRepository]).to(classOf[UpdateTaskRepositoryImpl])
    bind(classOf[LeagueRepository]).to(classOf[LeagueRepositoryImpl])
    bind(classOf[LiveGameRepository]).to(classOf[LiveGameRepositoryImpl])
    bind(classOf[ScheduledGameRepository]).to(classOf[ScheduledGameRepositoryImpl])
    bind(classOf[PickRepository]).to(classOf[PickRepositoryImpl])

    //service
    bind(classOf[TeamService]).to(classOf[TeamServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[UpdateTaskService]).to(classOf[UpdateTaskServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LeagueService]).to(classOf[LeagueServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LiveGameService]).to(classOf[LiveGameServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[PickService]).to(classOf[PickServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[ScheduledGameService]).to(classOf[ScheduledGameServiceImpl]).in(classOf[com.google.inject.Singleton])

  }
}

class MysqlModule extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[DB]).toInstance(MysqlDB)
  }
}

class H2Module extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[DB]).toInstance(H2DB)
  }
}