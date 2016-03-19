package com.eb.schedule.model.services

import com.eb.schedule.dto.{DTOUtils, LiveGameDTO}
import com.eb.schedule.model.dao.{PickRepository, LeagueRepository, TeamRepository, LiveGameRepository}
import com.eb.schedule.model.slick.{League, Team, Pick, LiveGame}
import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by Egor on 20.02.2016.
  */
trait LiveGameService {
  def findById(id: Long): LiveGameDTO

  def exists(id: Long): Future[Boolean]

  def insert(matchDetails: LiveGameDTO): Future[Int]

  def update(matchDetails: LiveGameDTO): Future[Int]

  def delete(id: Long): Future[Int]
}

class LiveGameServiceImpl @Inject()(liveGameRepository: LiveGameRepository, teamRepository: TeamRepository, leagueRepository: LeagueRepository, pickRepository: PickRepository) extends LiveGameService {
  def findById(id: Long): LiveGameDTO = {
    val liveGame = for {
      game <- liveGameRepository.findById(id)
      radiant <- teamRepository.findById(game.radiant)
      dire <- teamRepository.findById(game.dire)
    } yield DTOUtils.createLiveGameWithTeamDTO(game, radiant.get, dire.get)
    val picks: Seq[Pick] = Await.result(pickRepository.findByMatchId(id), Duration.Inf)
    val liveGameDTO: LiveGameDTO = Await.result(liveGame, Duration.Inf)
    DTOUtils.fillLiveGameWithPicks(liveGameDTO, picks)
    liveGameDTO
  }

  def exists(id: Long): Future[Boolean] = {
    liveGameRepository.exists(id)
  }

  def insert(liveGameDTO: LiveGameDTO): Future[Int] = {
    val radiantPicks: List[Pick] = DTOUtils.transformPickFromDTO(liveGameDTO.matchId, liveGameDTO.radiant, true)
    val direPicks: List[Pick] = DTOUtils.transformPickFromDTO(liveGameDTO.matchId, liveGameDTO.dire, false)
    val radiantTeam: Team = DTOUtils.transformTeamFromDTO(liveGameDTO.radiant)
    val direTeam: Team = DTOUtils.transformTeamFromDTO(liveGameDTO.dire)
    val league: League = DTOUtils.transformLeagueFromDTO(liveGameDTO.leagueDTO)
    val liveGame: LiveGame = DTOUtils.transformLiveGameFromDTO(liveGameDTO)
    val liveGameFuture = for {
      r <- teamRepository.insertTeamTask(radiantTeam)
      d <- teamRepository.insertTeamTask(direTeam)
      l <- leagueRepository.insertLeagueTask(league)
      rp <- pickRepository.insert(radiantPicks)
      dp <- pickRepository.insert(direPicks)
      g <- liveGameRepository.insert(liveGame)
    } yield g

    liveGameFuture
  }

  def update(liveGameDTO: LiveGameDTO): Future[Int] = {
    val liveGame: LiveGame = DTOUtils.transformLiveGameFromDTO(liveGameDTO)
    liveGameRepository.update(liveGame)
  }

  def delete(id: Long): Future[Int] = {
    liveGameRepository.delete(id)
  }
}
