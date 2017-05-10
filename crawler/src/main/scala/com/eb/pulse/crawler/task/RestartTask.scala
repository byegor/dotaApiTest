package com.eb.pulse.crawler.task

import com.eb.schedule.dao.SeriesRepository
import com.eb.schedule.model.slick.MatchSeries

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Iegor.Bondarenko on 04.05.2017.
  */
class RestartTask(val seriesRepository: SeriesRepository) {

  def processRestart(): Unit = {
    val stillRunningMatches: Future[Seq[MatchSeries]] = seriesRepository.getLiveMatches()
    stillRunningMatches.map(seq => seq.foreach(liveMatch => seriesRepository.updateFinishedState(liveMatch.matchId, finished = true)))
  }

}
