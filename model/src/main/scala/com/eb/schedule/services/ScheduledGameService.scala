package com.eb.schedule.model.services

import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}

import com.eb.schedule.dto.{CurrentGameDTO, ScheduledGameDTO, SeriesDTO}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.services.SeriesService
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 20.02.2016.
  */
trait ScheduledGameService {
  def findById(id: Int): Future[ScheduledGameDTO]

  /*def findByMatchId(matchId: Long): Future[Option[ScheduledGameDTO]]*/

  def exists(id: Int): Future[Boolean]

  def insert(game: ScheduledGameDTO): Future[Int]

  def insertAndGet(game: ScheduledGameDTO): Int

  def update(game: ScheduledGameDTO): Future[Int]

  def updateStatus(id: Int, status: MatchStatus): Future[Int]

  /*def updateStatusByMatchId(id: Long, status: Byte): Future[Int]*/

  def delete(id: Int): Future[Int]

  def getScheduledGames(matchDetails: CurrentGameDTO): Option[ScheduledGameDTO]

  def getScheduledGames(matchDetails: CurrentGameDTO, matchStatus: MatchStatus): Option[ScheduledGameDTO]

  def getScheduledGamesByStatus(matchStatus: MatchStatus): Future[Seq[ScheduledGameDTO]]

  def getRecentGames(millis: Long): Future[Map[ScheduledGameDTO, Seq[Option[SeriesDTO]]]]
}


class ScheduledGameServiceImpl @Inject()(repository: ScheduledGameRepository, seriesService: SeriesService) extends ScheduledGameService {

  private val log = LoggerFactory.getLogger(this.getClass)

  def findById(id: Int): Future[ScheduledGameDTO] = {
    repository.findById(id).map(DTOUtils.crateDTO)
  }

  def exists(id: Int): Future[Boolean] = {
    repository.exists(id)
  }

  def insert(game: ScheduledGameDTO): Future[Int] = {
    repository.insert(DTOUtils.transformScheduledGameFromDTO(game))
  }

  def insertAndGet(game: ScheduledGameDTO): Int = {
    val id = Await.result(repository.insertAndGet(DTOUtils.transformScheduledGameFromDTO(game)), Duration.Inf)
    id
  }

  def update(game: ScheduledGameDTO): Future[Int] = {
    repository.update(DTOUtils.transformScheduledGameFromDTO(game))
  }

  def updateStatus(id: Int, status: MatchStatus): Future[Int] = {
    repository.updateStatus(id, status.status)
  }


  def delete(id: Int): Future[Int] = {
    repository.delete(id)
  }

  def getScheduledGames(liveGameDTO: CurrentGameDTO): Option[ScheduledGameDTO] = {
    if (liveGameDTO.radiantTeam.id == -1 && liveGameDTO.radiantTeam.id == liveGameDTO.direTeam.id) {
      None
    } else {
      val future: Future[Seq[ScheduledGame]] = repository.getScheduledGames(liveGameDTO.radiantTeam.id, liveGameDTO.direTeam.id, liveGameDTO.basicInfo.league.leagueId, liveGameDTO.basicInfo.seriesType)
      val result: Seq[ScheduledGame] = Await.result(future, Duration.Inf)
      val now: Long = System.currentTimeMillis()
      val hoursForTheGame = TimeUnit.HOURS.toMillis(liveGameDTO.basicInfo.seriesType.gamesCount + 1)
      //todo move to enum bo1
      val maybeScheduledGame: Option[ScheduledGame] = result.find(game => ((now - game.startDate.getTime) < hoursForTheGame) && isGamesNumberMatches(liveGameDTO, game))
      if (maybeScheduledGame.isDefined) {
        Some(DTOUtils.crateDTO(maybeScheduledGame.get))
      } else {
        None
      }
    }
  }

  private def isGamesNumberMatches(liveGameDTO: CurrentGameDTO, game: ScheduledGame) = {
    val seriesId: Seq[SeriesDTO] = Await.result(seriesService.findBySeriesId(game.id), Duration.Inf)
    val lastGame = seriesId.maxBy(series => series.gameNumber)
    if (lastGame.matchId == liveGameDTO.matchId ) {
      true
    } else {
      lastGame.gameNumber < (liveGameDTO.basicInfo.radiantWin + liveGameDTO.basicInfo.radiantWin + 1)
    }
  }

  def getScheduledGames(liveGameDTO: CurrentGameDTO, matchStatus: MatchStatus): Option[ScheduledGameDTO] = {
    val future: Future[Option[ScheduledGame]] = repository.getScheduledGames(liveGameDTO.radiantTeam.id, liveGameDTO.direTeam.id, liveGameDTO.basicInfo.league.leagueId, matchStatus)
    val result: Option[ScheduledGame] = Await.result(future, Duration.Inf)
    result match {
      case Some(g) => Some(DTOUtils.crateDTO(g))
      case None => None
    }
  }

  def getScheduledGamesByStatus(matchStatus: MatchStatus): Future[Seq[ScheduledGameDTO]] = {
    repository.getScheduledGamesByStatus(matchStatus).map(f => f.map(game => DTOUtils.crateDTO(game)))
  }

  def getRecentGames(millis: Long): Future[Map[ScheduledGameDTO, Seq[Option[SeriesDTO]]]] = {
    val c = Calendar.getInstance()
    c.setTimeInMillis(millis)
    c.add(Calendar.HOUR_OF_DAY, -24)
    val start = c.getTimeInMillis
    val end = millis + TimeUnit.SECONDS.toMillis(10)


    val gamesByDate: Future[Seq[(ScheduledGame, Option[MatchSeries])]] = repository.getGamesBetweenDate(new Timestamp(start), new Timestamp(end))
    val gamesDto: Future[Seq[(ScheduledGameDTO, Option[SeriesDTO])]] = gamesByDate.map(seq => seq.map(game => (DTOUtils.crateDTO(game._1), DTOUtils.crateMatchDTO(game._2))))
    val map: Future[Map[ScheduledGameDTO, Seq[Option[SeriesDTO]]]] = gamesDto.map(seq => seq.groupBy(_._1).mapValues(_.map(_._2)))
    map.onFailure {
      case ex => log.error("couldn't get games between dates " + new Date(millis), ex)
    }
    map
  }

}