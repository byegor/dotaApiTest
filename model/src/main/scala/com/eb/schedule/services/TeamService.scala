package com.eb.schedule.model.services

import com.eb.schedule.dto.TeamDTO
import com.eb.schedule.model.dao.TeamRepository
import com.eb.schedule.model.slick.Team
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait TeamService {
  def findById(id: Int): Future[Option[TeamDTO]]

  def exists(id: Int): Future[Boolean]

  def insert(team: TeamDTO): Future[Int]

  def insertTeamTask(t:TeamDTO):Future[Unit]

  def saveOrUpdateTeamAndTask(team: TeamDTO)

  def update(team: Team): Future[Int]

  def delete(id: Int): Future[Int]
}


class TeamServiceImpl @Inject()(teamRepository: TeamRepository) extends TeamService {
  def findById(id: Int): Future[Option[TeamDTO]] = {
    teamRepository.findById(id).map(DTOUtils.crateTeamDTO)
  }

  def exists(id: Int): Future[Boolean] = {
    teamRepository.exists(id)
  }

  def insert(team: TeamDTO): Future[Int] = {
    teamRepository.insert(DTOUtils.transformTeamFromDTO(team))
  }

  def insertTeamTask(t:TeamDTO):Future[Unit] = {
    teamRepository.insertTeamTask(DTOUtils.transformTeamFromDTO(t))
  }

  def saveOrUpdateTeamAndTask(team: TeamDTO) = {
    teamRepository.saveOrUpdateTeamAndTask(DTOUtils.transformTeamFromDTO(team))
  }

  def update(team: Team): Future[Int] = {
    teamRepository.update(team)
  }

  def delete(id: Int): Future[Int] = {
    teamRepository.delete(id)
  }
}