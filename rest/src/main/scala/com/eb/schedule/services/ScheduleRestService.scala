package com.eb.schedule.services

import java.time.Instant
import java.time.temporal.{ChronoField, ChronoUnit}
import java.util.Date

import com.eb.schedule.cache.CacheHelper
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.shared.bean.{GameBean, LeagueBean, TeamBean}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by Egor on 04.06.2016.
  */
trait ScheduleRestService {
  def getGameByDate(milliseconds: Long): List[GameBean]
}

class ScheduledRestServiceImpl @Inject()(scheduledGameService: ScheduledGameService, cacheHelper: CacheHelper) extends ScheduleRestService {

  private val log = LoggerFactory.getLogger(this.getClass)

  def getGameByDate(milliseconds: Long): List[GameBean] = {
    val games: ListBuffer[GameBean] = new ListBuffer[GameBean];
    val gamesBetweenDate: Map[ScheduledGameDTO, Seq[Option[SeriesDTO]]] = Await.result(scheduledGameService.getGamesBetweenDate(milliseconds), Duration.Inf)
    for ((game, matches) <- gamesBetweenDate) {
      val radiantTeam: TeamDTO = cacheHelper.getTeam(game.radiantTeam.id)
      val direTeam: TeamDTO = cacheHelper.getTeam(game.direTeam.id)
      val league: LeagueDTO = cacheHelper.getLeague(game.league.leagueId)
      val gameBean = new GameBean(game.id, game.startDate.getTime, new TeamBean(radiantTeam.id, radiantTeam.name, radiantTeam.tag, radiantTeam.logo),
        new TeamBean(direTeam.id, direTeam.name, direTeam.tag, direTeam.logo), new LeagueBean(league.leagueId, league.leagueName), game.seriesType.name(), 0, 0, game.matchStatus.status)
      matches.filter(option => option.isDefined).map(option => option.get)
        .filter(matches => matches.radiantWin.isDefined).foreach(matches => if (matches.radiantWin.get) gameBean.radiantWin = gameBean.radiantWin + 1 else gameBean.direWin = gameBean.direWin + 1)
      games += gameBean
    }
    games.toList
  }


}
