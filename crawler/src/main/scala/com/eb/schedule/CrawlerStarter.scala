package com.eb.schedule

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.configure.{MysqlModule, H2Module, CoreModule}
import com.eb.schedule.crawler.{LiveMatchCrawler, TeamCrawler}
import com.eb.schedule.model.services._
import com.google.inject.Guice


/**
  * Created by Егор on 08.02.2016.
  */
object CrawlerStarter extends App {
//  private val teamCrawler: TeamCrawler = new TeamCrawler()
//  private val liveMatchCrawler: LiveMatchCrawler = new LiveMatchCrawler()

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2);
  val injector = Guice.createInjector(new MysqlModule, new CoreModule)
  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[LeagueService])
  val liveGameService = injector.getInstance(classOf[LiveGameService])
  val pickService = injector.getInstance(classOf[PickService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])

  private val liveMatchCrawler: LiveMatchCrawler = new LiveMatchCrawler(teamService, liveGameService, pickService, scheduledGameService)
  private val teamCrawler: TeamCrawler = new TeamCrawler(teamService, taskService)

  executor.scheduleAtFixedRate(liveMatchCrawler, 0, 1, TimeUnit.MINUTES)
  executor.scheduleAtFixedRate(teamCrawler, 0, 10, TimeUnit.MINUTES)


}
