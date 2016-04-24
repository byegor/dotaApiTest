package com.eb.schedule.config

import com.eb.schedule.cache.{HeroCache, ItemCache, LeagueCache, TeamCache}
import com.eb.schedule.live.{LiveGameHelper, LiveGameProcessor, RestartProcessor}
import com.google.inject.AbstractModule

/**
  * Created by Egor on 26.03.2016.
  */
class RestModule extends AbstractModule {

  override def configure(): Unit = {

    //cache
    bind(classOf[HeroCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[ItemCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[LeagueCache]).in(classOf[com.google.inject.Singleton])
    bind(classOf[TeamCache]).in(classOf[com.google.inject.Singleton])

    //helpers
    bind(classOf[LiveGameHelper])

    //hilevel
    bind(classOf[RestartProcessor])
    bind(classOf[LiveGameProcessor])

  }

}
