package com.eb.schedule

import java.util.concurrent.{Executors, ScheduledExecutorService}

import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.crawler.{LeagueCrawler, TeamCrawlerRunner}
import com.eb.schedule.model.services._
import com.eb.schedule.services.ItemService
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
  val scheduledGameService = injector.getInstance(classOf[ScheduledGameService])
  val itemService = injector.getInstance(classOf[ItemService])

  private val teamCrawler: TeamCrawlerRunner = new TeamCrawlerRunner(teamService, taskService)

//  private val itemsCrawler:ItemsCrawler = new ItemsCrawler(itemService);
  private val leagueCrawler:LeagueCrawler = new LeagueCrawler(leagueService);
  leagueCrawler.run()
//  itemsCrawler.run()
  Thread.sleep(20000)
//  executor.scheduleAtFixedRate(teamCrawler, 0, 10, TimeUnit.MINUTES)


}
