package com.eb.schedule.crawler

import java.util.concurrent.TimeUnit

import com.eb.schedule.dto.{ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.services.SeriesService
import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import com.google.inject.Inject
import org.slf4j.LoggerFactory
//todo start count time not from starting the series but from the last game. Start from TEST
class LongRunningGamesCrawler @Inject()(seriesService: SeriesService, scheduledGameService: ScheduledGameService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)
  private val ONE_HOUR: Long = TimeUnit.HOURS.toMillis(1)

  override def run(): Unit = {
    try {
      val series: Map[ScheduledGameDTO, Seq[SeriesDTO]] = seriesService.getUnfinishedSeries()
      val longRunningSeries: Map[ScheduledGameDTO, Seq[SeriesDTO]] = series.filter(tuple => (System.currentTimeMillis() - tuple._1.startDate.getTime) > (tuple._1.seriesType.gamesCount * ONE_HOUR + 2 * ONE_HOUR))
      for ((game, series) <- longRunningSeries) {
        val runningGames: Seq[SeriesDTO] = series.filter(!_.finished)
        if (runningGames.nonEmpty) {
          log.error("found long running series: " + runningGames)
          runningGames.foreach(ser => seriesService.updateFinishedState(ser.matchId, true))
        } else {
          scheduledGameService.updateStatus(game.id, MatchStatus.FINISHED)
          log.error("found long running game: " + game.id)
        }
      }
    } catch {
      case e: Throwable => log.error("", e)
    }
  }

  def isFinished(series: SeriesDTO): Boolean = {
    val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_MATCH_DETAILS + series.matchId)
    val result: JsonObject = response.getAsJsonObject("result")
    result != null && !result.has("error")
  }
}
