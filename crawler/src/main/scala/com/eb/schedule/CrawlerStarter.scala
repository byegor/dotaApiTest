package com.eb.schedule

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.task.{FindFinishedGamesTask, FindLeagueTask, FindLongRunningGameTask, FindWinnerForTheMatchTask}


/**
  * Created by Егор on 08.02.2016.
  */
//todo remove empty scheduled games without series
//todo check rehost
object CrawlerStarter extends App {

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  private val seriesCrawler: FindFinishedGamesTask = new FindFinishedGamesTask(Lookup.gameService, Lookup.matchService)
  private val leagueCrawler: FindLeagueTask = new FindLeagueTask(Lookup.leagueService, Lookup.taskService, Lookup.httpUtils)
  private val winnerCrawler: FindWinnerForTheMatchTask = new FindWinnerForTheMatchTask(Lookup.matchService, Lookup.httpUtils)
  private val longRunningCrawler: FindLongRunningGameTask = new FindLongRunningGameTask(Lookup.gameService, Lookup.matchService, Lookup.httpUtils)
  //  private val itemsCrawler: ItemsCrawler = new ItemsCrawler(itemService, httpUtils)


  executor.scheduleAtFixedRate(leagueCrawler, 0, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(winnerCrawler, 10, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(seriesCrawler, 20, 60, TimeUnit.SECONDS)
  executor.scheduleAtFixedRate(longRunningCrawler, 30, 60, TimeUnit.SECONDS)
}
