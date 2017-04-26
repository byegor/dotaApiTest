package com.eb.schedule.configure

import com.eb.schedule.dao._
import com.eb.schedule.model.dao._
import com.eb.schedule.model.services._
import com.eb.schedule.services._
import com.eb.schedule.utils.HttpUtils
import com.google.inject.AbstractModule

/**
  * Created by Egor on 09.03.2016.
  */
class CoreModule extends AbstractModule{
  override def configure(): Unit = {
    //repository
    bind(classOf[ItemRepository]).to(classOf[ItemRepositoryImpl])
    bind(classOf[TeamRepository]).to(classOf[TeamRepositoryImpl])
    bind(classOf[UpdateTaskRepository]).to(classOf[UpdateTaskRepositoryImpl])
    bind(classOf[LeagueRepository]).to(classOf[LeagueRepositoryImpl])
    bind(classOf[ScheduledGameRepository]).to(classOf[ScheduledGameRepositoryImpl])
    bind(classOf[NetWorthRepository]).to(classOf[NetWorthRepositoryImpl])
    bind(classOf[SeriesRepository]).to(classOf[SeriesRepositoryImpl])

    //service
    bind(classOf[ItemService]).to(classOf[ItemServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[TeamService]).to(classOf[TeamServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[UpdateTaskService]).to(classOf[UpdateTaskServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LeagueService]).to(classOf[LeagueServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[ScheduledGameService]).to(classOf[ScheduledGameServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[NetWorthService]).to(classOf[NetWorthServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[SeriesService]).to(classOf[SeriesServiceImpl]).in(classOf[com.google.inject.Singleton])

    //utils
    bind(classOf[HttpUtils])
  }
}

class MysqlModule extends AbstractModule{
  override def configure(): Unit = {
//    bind(classOf[DB]).toInstance(MysqlDB)
  }
}

class H2Module extends AbstractModule{
  override def configure(): Unit = {
//    bind(classOf[DB]).toInstance(H2DB)
  }
}