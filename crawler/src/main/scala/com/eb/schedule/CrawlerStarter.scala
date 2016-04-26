package com.eb.schedule

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.crawler.{ItemsCrawler, LeagueCrawler, TeamCrawlerRunner}
import com.eb.schedule.model.services._
import com.eb.schedule.services.ItemService
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Guice


/**
  * Created by Егор on 08.02.2016.
  */
object CrawlerStarter extends App {

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(3);
  val injector = Guice.createInjector(new MysqlModule, new CoreModule)
  val teamService = injector.getInstance(classOf[TeamService])
  val taskService = injector.getInstance(classOf[UpdateTaskService])
  val leagueService = injector.getInstance(classOf[LeagueService])
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])
  val itemService = injector.getInstance(classOf[ItemService])
  val httpUtils = injector.getInstance(classOf[HttpUtils])

  private val teamCrawler: TeamCrawlerRunner = new TeamCrawlerRunner(teamService, taskService, httpUtils)
  private val leagueCrawler: LeagueCrawler = new LeagueCrawler(leagueService, taskService, httpUtils)
  private val itemsCrawler: ItemsCrawler = new ItemsCrawler(itemService, httpUtils)


  executor.scheduleAtFixedRate(teamCrawler, 0, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(leagueCrawler, 10, 60, TimeUnit.SECONDS)
//  executor.scheduleAtFixedRate(itemsCrawler, 20, 60, TimeUnit.SECONDS)



}
