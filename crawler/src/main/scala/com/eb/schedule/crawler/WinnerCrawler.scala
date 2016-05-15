package com.eb.schedule.crawler

import com.eb.schedule.dto.SeriesDTO
import com.eb.schedule.services.SeriesService
import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WinnerCrawler @Inject()(seriesService: SeriesService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def run(): Unit = {
    try{
    val series: Future[Seq[SeriesDTO]] = seriesService.getSeriesWithoutWinner()
    series.map(seq => seq.foreach(updateWinners))
    }catch {
      case e: Throwable => log.error("", e)
    }
  }

  def updateWinners(series: SeriesDTO): Unit = {
    val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_MATCH_DETAILS + series.matchId)
    val result: JsonObject = response.getAsJsonObject("result")
    if (result != null && !result.has("error")) {
      series.radiantWin = Some(result.get("radiant_win").getAsBoolean)
      seriesService.update(series)
      log.debug("Winner updated for matchId: " + series.matchId + " and seriesId: " + series.gameId)
    }
  }
}
