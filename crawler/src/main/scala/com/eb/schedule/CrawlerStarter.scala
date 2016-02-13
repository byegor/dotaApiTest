package com.eb.schedule

import java.nio.file.{Files, Paths}
import java.util.concurrent.{TimeUnit, Executors, ScheduledExecutorService}

import com.eb.schedule.crawler.{LiveMatchCrawler, TeamCrawler}
import com.eb.schedule.utils.DBUtils
import egor.dota.model.entity.TeamInfo

import scala.collection.JavaConversions._

/**
  * Created by Егор on 08.02.2016.
  */
object CrawlerStarter extends App {
  private val teamCrawler: TeamCrawler = new TeamCrawler()
  private val liveMatchCrawler: LiveMatchCrawler = new LiveMatchCrawler()

  private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2);

  executor.scheduleAtFixedRate(liveMatchCrawler, 0, 1, TimeUnit.MINUTES)
  executor.scheduleAtFixedRate(teamCrawler, 0, 10, TimeUnit.MINUTES)


}
