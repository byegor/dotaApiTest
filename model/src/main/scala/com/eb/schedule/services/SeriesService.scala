package com.eb.schedule.services

import com.eb.schedule.dao.SeriesRepository
import com.eb.schedule.dto.SeriesDTO
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait SeriesService {
  def findBySeriesId(id: Int): Future[Seq[SeriesDTO]]

  def exists(id: Int): Future[Boolean]

  def insert(series: SeriesDTO)
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
}
