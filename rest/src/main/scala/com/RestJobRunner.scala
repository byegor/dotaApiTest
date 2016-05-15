package com

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.config.RestModule
import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.live.{LiveGameHelper, LiveGameProcessor, RestartProcessor}
import com.eb.schedule.model.services.{LeagueService, ScheduledGameService, TeamService, UpdateTaskService}
import com.eb.schedule.services.{ItemService, NetWorthService, SeriesService}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Guice

/**
  * Created by Egor on 18.04.2016.
  */
object RestJobRunner {

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(3);
  val injector = Guice.createInjector(new MysqlModule, new CoreModule, new RestModule)


  //  private val restartProcessor: RestartProcessor = injector.getInstance(classOf[RestartProcessor])

  def start() = {
    val liveGameProcessor: LiveGameProcessor = injector.getInstance(classOf[LiveGameProcessor])
    //    todo restartProcessor.process()
    executor.scheduleAtFixedRate(liveGameProcessor, 0, 60, TimeUnit.SECONDS)
  }


}
