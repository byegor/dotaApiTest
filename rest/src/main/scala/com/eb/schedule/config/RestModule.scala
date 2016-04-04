package com.eb.schedule.config

import com.eb.schedule.cache.HeroCache
import com.google.inject.AbstractModule

/**
  * Created by Egor on 26.03.2016.
  */
class RestModule extends AbstractModule {

  override def configure(): Unit = {

    //cache
    bind(classOf[HeroCache]).in(classOf[com.google.inject.Singleton])
  }

}
