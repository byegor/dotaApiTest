package com.eb.schedule.model.services

import com.eb.schedule.model.dao.LiveGameRepository
import com.eb.schedule.model.slick.LiveGame
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait LiveGameService {
  def findById(id: Long): Future[LiveGame]

  def exists(id: Long): Future[Boolean]

  def insert(matchDetails: LiveGame): Future[Int]

  def update(id: Long, matchDetails: LiveGame): Future[Int]

  def delete(id: Long): Future[Int]
}

class LiveGameServiceImpl @Inject()(repository: LiveGameRepository) extends LiveGameService {
  def findById(id: Long): Future[LiveGame] = {
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
