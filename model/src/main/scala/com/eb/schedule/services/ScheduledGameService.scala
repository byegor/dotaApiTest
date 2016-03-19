package com.eb.schedule.model.services

import com.eb.schedule.dto.{ScheduledGameDTO, DTOUtils, LiveGameDTO}
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.slick.{LiveGame, ScheduledGame}
import com.google.inject.Inject

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
trait ScheduledGameService {
  def findById(id: Int): Future[ScheduledGame]

  def findByMatchId(matchId: Long): Future[Option[ScheduledGame]]

  def exists(id: Int): Future[Boolean]

  def insert(game: ScheduledGameDTO): Future[Int]

  def update(game: ScheduledGame): Future[Int]

  def updateStatus(id: Int, status: Byte): Future[Int]

  def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int]

  def delete(id: Int): Future[Int]

  def getScheduledGames(matchDetails: LiveGameDTO): Option[ScheduledGameDTO]
}


class ScheduledGameServiceImpl @Inject()(repository: ScheduledGameRepository) extends ScheduledGameService {
  def findById(id: Int): Future[ScheduledGame] = {
    repository.findById(id)
  }

  def findByMatchId(matchId: Long): Future[Option[ScheduledGame]] = {
    repository.findByMatchId(matchId)
  }

  def exists(id: Int): Future[Boolean] = {
    repository.exists(id)
  }

  def insert(game: ScheduledGameDTO): Future[Int] = {
    repository.insert(DTOUtils.transformScheduledGameFromDTO(game))
  }

  def update(game: ScheduledGame): Future[Int] = {
    repository.update(game)
  }

  def updateStatus(id: Int, status: Byte): Future[Int] = {
    repository.updateStatus(id, status)
  }

  def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int] = {
    repository.updateScore(matchId, radiantScore, direScore)
  }

  def delete(id: Int): Future[Int] = {
    repository.delete(id)
  }

  def getScheduledGames(liveGameDTO: LiveGameDTO): Option[ScheduledGameDTO] = {
    val future: Future[Option[ScheduledGame]] = repository.getScheduledGames(DTOUtils.transformLiveGameFromDTO(liveGameDTO))
    val result: Option[ScheduledGame] = Await.result(future, Duration.Inf)
    result match {
      case Some(g) => Some(DTOUtils.crateDTO(g))
      case None => None
    }
  }


}