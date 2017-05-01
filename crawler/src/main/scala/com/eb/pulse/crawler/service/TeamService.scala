package com.eb.pulse.crawler.service

import com.eb.schedule.model.dao.TeamRepositoryImpl

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
class TeamService(repository:TeamRepositoryImpl) {

  def findByTeamId(id:Int)={
    repository.findById(id)
  }

}
