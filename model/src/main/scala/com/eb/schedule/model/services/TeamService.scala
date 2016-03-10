package com.eb.schedule.model.services

import com.eb.schedule.model.dao.TeamRepository
import com.eb.schedule.model.slick.Team
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait TeamService {
  def findById(id: Int): Future[Option[Team]]

  def exists(id: Int): Future[Boolean]

  def insert(team: Team): Future[Int]

  def insertTeamTask(id: Int)

  def saveOrUpdateTeamAndTask(team: Team)

  def update(team: Team): Future[Int]

  def delete(id: Int): Future[Int]
}


class TeamServiceImpl @Inject()(teamRepository: TeamRepository) extends TeamService {
  def findById(id: Int): Future[Option[Team]] = {
    teamRepository.findById(id)
  }

  def exists(id: Int): Future[Boolean] = {
    teamRepository.exists(id)
  }

  def insert(team: Team): Future[Int] = {
    teamRepository.insert(team)
  }

  def insertTeamTask(id: Int) = {
    teamRepository.insertTeamTask(id)
  }

  def saveOrUpdateTeamAndTask(team: Team) = {
    teamRepository.saveOrUpdateTeamAndTask(team)
  }

  def update(team: Team): Future[Int] = {
    teamRepository.update(team)
  }

  def delete(id: Int): Future[Int] = {
    teamRepository.delete(id)
  }
}