package com.eb.pulse.crawler.service

import com.eb.schedule.model.dao.TeamRepositoryImpl
import com.eb.schedule.model.slick.Team

import scala.concurrent.Future

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
class TeamService(repository: TeamRepositoryImpl) {

  def findByTeamId(id: Int) = {
    repository.findById(id)
  }

  def upsertTeam(team: Team): Future[Unit] = {
    repository.upsert(team)
  }

}
