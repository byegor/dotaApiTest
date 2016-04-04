package com.eb.schedule.configure

import com.eb.schedule.dao.{ItemRepositoryImpl, ItemRepository, HeroRepositoryImpl, HeroRepository}
import com.eb.schedule.model.dao._
import com.eb.schedule.model.db.{DB, H2DB, MysqlDB}
import com.eb.schedule.model.services._
import com.eb.schedule.services.{ItemServiceImpl, ItemService, HeroServiceImpl, HeroService}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.AbstractModule

/**
  * Created by Egor on 09.03.2016.
  */
class CoreModule extends AbstractModule{
  override def configure(): Unit = {
    //repository
    bind(classOf[HeroRepository]).to(classOf[HeroRepositoryImpl])
    bind(classOf[ItemRepository]).to(classOf[ItemRepositoryImpl])
    bind(classOf[TeamRepository]).to(classOf[TeamRepositoryImpl])
    bind(classOf[UpdateTaskRepository]).to(classOf[UpdateTaskRepositoryImpl])
    bind(classOf[LeagueRepository]).to(classOf[LeagueRepositoryImpl])
    bind(classOf[ScheduledGameRepository]).to(classOf[ScheduledGameRepositoryImpl])

    //service
    bind(classOf[HeroService]).to(classOf[HeroServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[ItemService]).to(classOf[ItemServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[TeamService]).to(classOf[TeamServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[UpdateTaskService]).to(classOf[UpdateTaskServiceImpl]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LeagueService]).to(classOf[LeagueServiceImpl]).in(classOf[com.google.inject.Singleton])
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