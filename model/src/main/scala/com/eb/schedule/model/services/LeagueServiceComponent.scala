package com.eb.schedule.model.services

import com.eb.schedule.model.dao.{LeagueRepComp, LeagueRepositoryComponent}
import com.eb.schedule.model.slick.{League, League}

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait LeagueServiceComponent {

  def leagueService: LeagueService

  trait LeagueService {
    def findById(id: Int): Future[League]

    def exists(id: Int): Future[Boolean]

    def insert(league: League): Future[Int]

    def insertLeagueTask(id: Int)

    def update(id: Int, league: League): Future[Int]

    def delete(id: Int): Future[Int]
  }

}

trait LeagueServiceImplComponent extends LeagueServiceComponent {
  this: LeagueRepComp =>

  def leagueService = new LeagueServiceImpl

  class LeagueServiceImpl extends LeagueService {
    def findById(id: Int): Future[League] = {
      leagueRep.findById(id)
    }

    def exists(id: Int): Future[Boolean] = {
      leagueRep.exists(id)
    }

    def insert(league: League): Future[Int] = {
      leagueRep.insert(league)
    }

    def insertLeagueTask(id: Int) = {
      leagueRep.insertLeagueTask(id)
    }

    def update(id: Int, league: League): Future[Int] = {
      leagueRep.update(id, league)
    }

    def delete(id: Int): Future[Int] = {
      leagueRep.delete(id)
    }
  }

}