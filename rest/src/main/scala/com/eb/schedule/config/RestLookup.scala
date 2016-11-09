package com.eb.schedule.config

import com.eb.schedule.cache.PlayerCache
import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.live.RestartProcessor
import com.eb.schedule.services.ScheduleRestService
import com.eb.schedule.task.LiveGameTask
import com.google.inject.Guice

/**
  * Created by Egor on 05.06.2016.
  */
object RestLookup {

  private val injector = Guice.createInjector(new MysqlModule, new CoreModule, new RestModule)

  val scheduleRestService: ScheduleRestService = injector.getInstance(classOf[ScheduleRestService])
  val playerCache: PlayerCache = injector.getInstance(classOf[PlayerCache])

  val liveGameProcessor: LiveGameTask = injector.getInstance(classOf[LiveGameTask])
  val restartProcessor: RestartProcessor = injector.getInstance(classOf[RestartProcessor])
}
