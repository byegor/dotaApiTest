package com

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.config.RestModule
import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.live.{LiveGameHelper, LiveGameProcessor}
import com.eb.schedule.model.services.{LeagueService, ScheduledGameService, TeamService, UpdateTaskService}
import com.eb.schedule.services.{ItemService, NetWorthService, SeriesService}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Guice

/**
  * Created by Egor on 18.04.2016.
  */
object RestTestMain extends App {

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(3);
  val injector = Guice.createInjector(new MysqlModule, new CoreModule, new RestModule)
  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[LeagueService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])
  val itemService = injector.getInstance(classOf[ItemService])
  val httpUtils = injector.getInstance(classOf[HttpUtils])
  val netWorthService = injector.getInstance(classOf[NetWorthService])
  val seriesService = injector.getInstance(classOf[SeriesService])

  val liveGameHelper: LiveGameHelper = injector.getInstance(classOf[LiveGameHelper])
  private val liveGameProcessor: LiveGameProcessor = new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, httpUtils)


  executor.scheduleAtFixedRate(liveGameProcessor, 0, 60, TimeUnit.SECONDS)

}
