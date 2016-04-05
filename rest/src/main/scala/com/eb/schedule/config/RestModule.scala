package com.eb.schedule.config

import com.eb.schedule.cache.{HeroCache, ItemCache, LeagueCache}
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
  }

}
