package com.eb.schedule.model.services

import com.eb.schedule.dto.LeagueDTO
import com.eb.schedule.model.dao.LeagueRepository
import com.eb.schedule.model.slick.League
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Egor on 20.02.2016.
  */
trait LeagueService {
  def findById(id: Int): Future[Option[LeagueDTO]]

  def exists(id: Int): Future[Boolean]

  def insert(league: LeagueDTO): Future[Int]

  def insertLeagueTask(leagueDTO: LeagueDTO)

  def update(league: LeagueDTO): Future[Int]

  def delete(id: Int): Future[Int]
}

class LeagueServiceImpl @Inject()(leagueRep: LeagueRepository) extends LeagueService {
  def findById(id: Int): Future[Option[LeagueDTO]] = {
    leagueRep.findById(id).map(DTOUtils.crateDTO)
  }

  def exists(id: Int): Future[Boolean] = {
    leagueRep.exists(id)
  }

  def insert(league: LeagueDTO): Future[Int] = {
    leagueRep.insert(DTOUtils.transformLeagueFromDTO(league))
  }

  def insertLeagueTask(leagueDTO: LeagueDTO) = {
    leagueRep.insertLeagueTask(DTOUtils.transformLeagueFromDTO(leagueDTO))
  }

  def update(league: LeagueDTO): Future[Int] = {
    leagueRep.update(DTOUtils.transformLeagueFromDTO(league))
  }

  def delete(id: Int): Future[Int] = {
    leagueRep.delete(id)
  }
}
