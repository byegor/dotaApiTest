package com.eb.schedule.model.services

import com.eb.schedule.model.dao.{LiveGameRepComp, LiveGameRepositoryComponent}
import com.eb.schedule.model.slick.{LiveGame, LiveGame}

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait LiveGameServiceComponent {

  def liveGameService: LiveGameService

  trait LiveGameService {
    def findById(id: Int): Future[LiveGame]

    def exists(id: Long): Future[Boolean]

    def insert(matchDetails: LiveGame): Future[Int]

    def update(id: Long, matchDetails: LiveGame): Future[Int]

    def delete(id: Long): Future[Int]
  }
}

trait LiveGameServiceImplComponent extends LiveGameServiceComponent {
  this: LiveGameRepComp =>

  def liveGameService = new LiveGameServiceImpl

  class LiveGameServiceImpl extends LiveGameService {
    def findById(id: Int): Future[LiveGame] = {
      repository.findById(id)
    }

    def exists(id: Long): Future[Boolean] = {
      repository.exists(id)
    }

    def insert(liveGame: LiveGame): Future[Int] = {
      repository.insert(liveGame)
    }

    def update(id: Long, matchDetails: LiveGame): Future[Int] = {
      repository.update(id, matchDetails)
    }

    def delete(id: Long): Future[Int] = {
      repository.delete(id)
    }
  }
}