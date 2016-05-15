package com.eb.schedule.crawler

import com.eb.schedule.dto.{ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.services.SeriesService
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 10.05.2016.
  */
class SeriesCrawler @Inject()(seriesService: SeriesService, scheduledGameService: ScheduledGameService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def run(): Unit = {
    try {
      val series: Map[ScheduledGameDTO, Seq[SeriesDTO]] = seriesService.getUnfinishedSeries()
      val shouldBeProcessed: Map[ScheduledGameDTO, Seq[SeriesDTO]] = series.filterNot(tuple => tuple._2.exists(_.radiantWin.isEmpty))

      val games: Map[ScheduledGameDTO, Map[Option[Boolean], Seq[SeriesDTO]]] = shouldBeProcessed.map(tuple => (tuple._1, tuple._2.groupBy(_.radiantWin)))
      games.filter(tuple =>
        (tuple._2.getOrElse(Some(true), Nil).size * 2 > tuple._1.seriesType.gamesCount) || (tuple._2.getOrElse(Some(false), Nil).size * 2 > tuple._1.seriesType.gamesCount)
      ).keys.foreach(scheduledGame => {
        scheduledGameService.updateStatus(scheduledGame.id, MatchStatus.FINISHED)
        log.debug("Set series as finished for: " + scheduledGame.id)
      }
      )
    } catch {
      case e: Throwable => log.error("SeriesCrawler: ", e)
    }
  }
}
