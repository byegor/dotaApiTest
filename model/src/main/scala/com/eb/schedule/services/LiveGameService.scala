package com.eb.schedule.model.services

import com.eb.schedule.dto.{DTOUtils, LiveGameDTO}
import com.eb.schedule.model.dao.{PickRepository, LeagueRepository, TeamRepository, LiveGameRepository}
import com.eb.schedule.model.slick.{League, Team, Pick, LiveGame}
import com.google.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait LiveGameService {
  def findById(id: Long): Future[LiveGameDTO]

  def exists(id: Long): Future[Boolean]

  def insert(matchDetails: LiveGameDTO): Future[Int]

  def update(matchDetails: LiveGameDTO): Future[Int]

  def delete(id: Long): Future[Int]
}

class LiveGameServiceImpl @Inject()(liveGameRepository: LiveGameRepository, teamRepository: TeamRepository, leagueRepository: LeagueRepository, pickRepository: PickRepository) extends LiveGameService {
  def findById(id: Long): Future[LiveGameDTO] = {
    liveGameRepository.findById(id).map(DTOUtils.createLiveGameDTO)
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
    teamRepository.insertTeamTask(radiantTeam)
    teamRepository.insertTeamTask(direTeam)

    leagueRepository.insertLeagueTask(league)

    radiantPicks.foreach(pickRepository.insert)
    direPicks.foreach(pickRepository.insert)

    liveGameRepository.insert(liveGame)
  }

  def update(liveGameDTO: LiveGameDTO): Future[Int] = {
    val liveGame: LiveGame = DTOUtils.transformLiveGameFromDTO(liveGameDTO)
    liveGameRepository.update(liveGame)
  }

  def delete(id: Long): Future[Int] = {
    liveGameRepository.delete(id)
  }
}
