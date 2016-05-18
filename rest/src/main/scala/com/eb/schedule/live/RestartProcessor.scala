package com.eb.schedule.live

import com.eb.schedule.dto.SeriesDTO
import com.eb.schedule.services.SeriesService
import com.google.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 22.04.2016.
  */
class RestartProcessor @Inject()(val seriesService: SeriesService) {

  def process(): Unit = {
    val winner: Future[Seq[SeriesDTO]] = seriesService.getRunningSeries()
    winner.map(seq => seq.foreach(win => seriesService.updateFinishedState(win.matchId, true)))
  }
}
