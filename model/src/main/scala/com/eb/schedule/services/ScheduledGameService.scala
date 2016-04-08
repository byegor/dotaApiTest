package com.eb.schedule.model.services

import com.eb.schedule.dto.{DTOUtils, CurrentGameDTO, ScheduledGameDTO}
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.slick.ScheduledGame
import com.google.inject.Inject

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 20.02.2016.
  */
trait ScheduledGameService {
  def findById(id: Int): Future[ScheduledGame]

  def findByMatchId(matchId: Long): Future[Option[ScheduledGame]]

  def exists(id: Int): Future[Boolean]

  def insert(game: ScheduledGameDTO): Future[Int]

  def insertAndGet(game: ScheduledGameDTO): Future[Int]

  def update(game: ScheduledGame): Future[Int]

  def updateStatus(id: Int, status: Byte): Future[Int]

  def updateStatusByMatchId(id: Long, status: Byte): Future[Int]

  def delete(id: Int): Future[Int]

  def getScheduledGames(matchDetails: CurrentGameDTO): Option[ScheduledGameDTO]
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

  def insertAndGet(game: ScheduledGameDTO): Future[Int] ={
    repository.insertAndGet(DTOUtils.transformScheduledGameFromDTO(game))
  }

  def update(game: ScheduledGame): Future[Int] = {
    repository.update(game)
  }

  def updateStatus(id: Int, status: Byte): Future[Int] = {
    repository.updateStatus(id, status)
  }

  def updateStatusByMatchId(id: Long, status: Byte): Future[Int] ={
    repository.updateStatusByMatchId(id, status)
  }


  def delete(id: Int): Future[Int] = {
    repository.delete(id)
  }

  def getScheduledGames(liveGameDTO: CurrentGameDTO): Option[ScheduledGameDTO] = {
    val future: Future[Option[ScheduledGame]] = repository.getScheduledGames(liveGameDTO.radiantTeam.id, liveGameDTO.direTeam.id, liveGameDTO.basicInfo.league.leagueId)
    val result: Option[ScheduledGame] = Await.result(future, Duration.Inf)
    result match {
      case Some(g) => Some(DTOUtils.crateDTO(g))
      case None => None
    }
  }


}