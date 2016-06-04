package com.eb.schedule.config

import com.eb.schedule.cache._
import com.eb.schedule.task.LiveGameTask
import com.eb.schedule.live.{LiveGameHelper, RestartProcessor}
import com.eb.schedule.services.{ScheduleRestService, ScheduledRestServiceImpl}
import com.google.inject.AbstractModule

/**
  * Created by Egor on 26.03.2016.
  */
class RestModule extends AbstractModule {

  override def configure(): Unit = {

    //service
    bind(classOf[ScheduleRestService]).to(classOf[ScheduledRestServiceImpl])

    //cache
    bind(classOf[HeroCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[ItemCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LeagueCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[TeamCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[PlayerCache]).in(classOf[com.google.inject.Singleton])

    //helpers
    bind(classOf[LiveGameHelper])
    bind(classOf[CacheHelper])

    //hilevel
    bind(classOf[RestartProcessor])
    bind(classOf[LiveGameTask])

  }

}
