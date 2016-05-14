package com.eb.schedule.services

import com.eb.schedule.dao.SeriesRepository
import com.eb.schedule.dto.{ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 20.02.2016.
  */
trait SeriesService {
  def findBySeriesId(id: Int): Future[Seq[SeriesDTO]]

  def exists(id: Int): Future[Boolean]

  def insert(series: SeriesDTO): Future[Int]

  def update(series: SeriesDTO): Future[Int]

  def getUnfinishedSeries(): Map[ScheduledGameDTO, Seq[SeriesDTO]]

  def getSeriesWithoutWinner(): Future[Seq[SeriesDTO]]
}

class SeriesServiceImpl @Inject()(rep: SeriesRepository) extends SeriesService {
  def findBySeriesId(id: Int): Future[Seq[SeriesDTO]] = {
    rep.findSeriesId(id).map(seq => seq.map(DTOUtils.crateDTO))
  }

  def exists(id: Int): Future[Boolean] = {
    rep.exists(id)
  }

  def insert(series: SeriesDTO) = {
    rep.insert(DTOUtils.transformMatchSeriesFromDTO(series))
  }

  def update(series: SeriesDTO) = {
    rep.update(DTOUtils.transformMatchSeriesFromDTO(series))
  }

  def getUnfinishedSeries(): Map[ScheduledGameDTO, Seq[SeriesDTO]] = {
    val series: Seq[(ScheduledGame, MatchSeries)] = Await.result(rep.getUnfinishedSeries(), Duration.Inf)
    val unfinishedSeries: Seq[(ScheduledGameDTO, SeriesDTO)] = series.map(game => (DTOUtils.crateDTO(game._1), DTOUtils.crateDTO(game._2)))
    unfinishedSeries.groupBy(_._1).mapValues(_.map(_._2))
  }

  def getSeriesWithoutWinner(): Future[Seq[SeriesDTO]] = {
    rep.getSeriesWithoutWinner().map(future => future.map(DTOUtils.crateDTO))
  }
}
