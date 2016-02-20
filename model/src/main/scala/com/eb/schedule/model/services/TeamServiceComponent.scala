package com.eb.schedule.model.services

import com.eb.schedule.model.dao.TeamRepositoryComponent
import com.eb.schedule.model.slick.Team

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait TeamServiceComponent {

  def teamService:TeamService

  trait TeamService{
    def findById(id: Int): Future[Team]

    def exists(id: Int): Future[Boolean]

    def insert(team: Team): Future[Int]

    def insertTeamTask(id: Int)

    def saveOrUpdateTeamAndTask(team: Team)

    def update(team: Team): Future[Int]

    def delete(id: Int): Future[Int]
  }
}

trait TeamServiceImplComponent extends TeamServiceComponent{
  this: TeamRepositoryComponent =>

  def teamService = new TeamServiceImpl

  class TeamServiceImpl extends TeamService{
    def findById(id: Int): Future[Team] = {
      teamRepository.findById(id)
    }

    def exists(id: Int): Future[Boolean] = {
      teamRepository.exists(id)
    }

    def insert(team: Team): Future[Int] = {
      teamRepository.insert(team)
    }

    def insertTeamTask(id: Int) = ???

    def saveOrUpdateTeamAndTask(team: Team) = ???

    def update(team: Team): Future[Int] ={
      teamRepository.update(team)
    }

    def delete(id: Int): Future[Int] = {
      teamRepository.delete(id)
    }
  }
}