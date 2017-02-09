package com.eb.schedule.services

import com.eb.schedule.cache.{CacheHelper, CachedTeam}
import com.eb.schedule.dto._
import com.eb.schedule.live.GameContainer
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.shared.bean.{GameBean, LeagueBean, Match, TeamBean}
import com.google.inject.Inject
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 04.06.2016.
  */
//todo rehost
//todo cache results for 1 min
trait ScheduleRestService {
  def getGameByDate(milliseconds: Long): Map[String, Seq[GameBean]]

  def getGameMatchesById(gameId: Int): Seq[Match]

  def getMatchById(matchId: Long): Option[Match]
}

class ScheduledRestServiceImpl @Inject()(scheduledGameService: ScheduledGameService, seriesService: SeriesService, cacheHelper: CacheHelper) extends ScheduleRestService {

  private val log = LoggerFactory.getLogger(this.getClass)
  val formatter = DateTimeFormat.forPattern("EEEE, d MMM")

  def getGameByDate(milliseconds: Long): Map[String, Seq[GameBean]] = {
    val games: ListBuffer[GameBean] = new ListBuffer[GameBean]
    val gamesBetweenDate: Map[ScheduledGameDTO, Seq[Option[SeriesDTO]]] = Await.result(scheduledGameService.getRecentGames(milliseconds), Duration.Inf)
    for ((game, matches) <- gamesBetweenDate) {
      val radiantTeam: CachedTeam = cacheHelper.getTeam(game.radiantTeam.id)
      val direTeam: CachedTeam = cacheHelper.getTeam(game.direTeam.id)
      val league: LeagueDTO = cacheHelper.getLeague(game.league.leagueId)
      val gameBean = new GameBean(game.id, game.startDate.getTime, new TeamBean(radiantTeam.id, radiantTeam.name, radiantTeam.tag, radiantTeam.logo),
        new TeamBean(direTeam.id, direTeam.name, direTeam.tag, direTeam.logo), new LeagueBean(league.leagueId, league.leagueName), game.seriesType.name(), 0, 0, game.matchStatus.status)
      matches.filter(option => option.isDefined).map(option => option.get)
        .filter(matches => matches.radiantWin.isDefined).foreach(matches =>
        if (matches.radiantWin.get) gameBean.setRadiantWin(gameBean.radiantWin + 1) else gameBean.setDireWin(gameBean.direWin + 1)
      )
      games += gameBean
    }

    val by: Map[Long, ListBuffer[GameBean]] = games.groupBy(g => getMillisInUTC(g.startTime))
    val sortedByDays: ListMap[Long, ListBuffer[GameBean]] = ListMap(by.toSeq.sortWith(_._1 > _._1): _*)
    sortedByDays.map { case (k, v) => formatter.print(k) -> v.sortBy(g =>  (g.gameStatus, -g.startTime)) }
  }

  def sortByStartDateAndMatchStatus(g1: GameBean, g2: GameBean) = {
    g1.gameStatus < g2.gameStatus && g1.startTime < g2.startTime

  }

  def getMillisInUTC(mil: Long): Long = {
    new DateTime().withMillis(mil).withZone(DateTimeZone.UTC).withTimeAtStartOfDay().getMillis
  }

  override def getGameMatchesById(gameId: Int): Seq[Match] = {
    val finishedMatches: Seq[Match] = getGamesMatches(gameId)

    val matchId: Option[Long] = GameContainer.getLiveMatchIdByScheduledGameId(gameId)
    if (matchId.isDefined) {
      val game: Option[CurrentGameDTO] = GameContainer.getLiveGame(matchId.get)
      if (game.isDefined) {
        val liveMatch: CurrentGameDTO = game.get
        return finishedMatches.toList ::: List(liveMatch.toMatch())
      }
    }
    finishedMatches
  }

  def getGamesMatches(gameId: Int): Seq[Match] = {
    val series: Seq[SeriesDTO] = Await.result(seriesService.findBySeriesId(gameId), Duration.Inf)
    val gamesMatches: Seq[Match] = series.map(game => cacheHelper.getMatch(game.matchId)).filter(_.isDefined).map(_.get)
    gamesMatches
  }

  def getMatchById(matchId: Long): Option[Match] = {
    val liveGame: Option[CurrentGameDTO] = GameContainer.getLiveGame(matchId)
    if (liveGame.isDefined) {
      Some(liveGame.get.toMatch())
    } else {
      cacheHelper.getMatch(matchId)
    }
  }
}
