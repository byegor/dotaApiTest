package com.eb.pulse.crawler.data.service

import com.eb.schedule.model.dao.LeagueRepository
import com.eb.schedule.model.slick.League

import scala.concurrent.Future

/**
  * Created by Iegor.Bondarenko on 26.04.2017.
  */
class LeagueService(leagueRepository: LeagueRepository) {
  def insert(league: League): Future[Int] = {
    leagueRepository.insert(league)
  }

  def insertLeagueTask(league: League) = {
    leagueRepository.insertLeagueTask(league)
  }

  def findByLeagueId(id: Int) = {
    leagueRepository.findById(id)
  }


}
