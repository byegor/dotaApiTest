package com.eb.pulse.crawler

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.pulse.crawler.task._


/**
  * Created by Егор on 08.02.2016.
  */
//todo check rehost
object CrawlerStarter extends App {

  val restartTask = new RestartTask(Lookup.seriesRepository)
  restartTask.processRestart()

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

  private val seriesCrawler: FindFinishedGamesTask = new FindFinishedGamesTask(Lookup.gameService, Lookup.matchService)
  private val leagueCrawler: FindLeagueTask = new FindLeagueTask(Lookup.leagueService, Lookup.taskService, Lookup.httpUtils)
  private val winnerCrawler: FindWinnerForTheMatchTask = new FindWinnerForTheMatchTask(Lookup.matchService, Lookup.httpUtils)
  private val longRunningCrawler: FindLongRunningGameTask = new FindLongRunningGameTask(Lookup.gameService, Lookup.matchService, Lookup.httpUtils)
  private val heroTask = new DownloadHeroTask(Lookup.taskService, Lookup.httpUtils, Lookup.heroService, Lookup.heroCache)
  //  private val itemsCrawler: ItemsCrawler = new ItemsCrawler(itemService, httpUtils)

  private val liveMatchTask = new LiveMatchTask(Lookup.gameService, Lookup.matchService, Lookup.httpUtils, Lookup.netWorthService, Lookup.cacheHelper, Lookup.teamService)

  executor.scheduleAtFixedRate(liveMatchTask, 5, 40, TimeUnit.SECONDS)


  executor.scheduleAtFixedRate(leagueCrawler, 0, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(winnerCrawler, 10, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(seriesCrawler, 20, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(longRunningCrawler, 30, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(heroTask, 40, 60, TimeUnit.SECONDS)
}
