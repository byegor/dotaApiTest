package com.eb.pulse.crawler.data.service

import java.sql.Timestamp
import java.util.concurrent.TimeUnit
import java.util.{Calendar, Date}

import com.eb.pulse.crawler.data.model.LiveMatch
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 21.04.2017.
  */
class GameService(repository: ScheduledGameRepository) extends Service {
  private val log = LoggerFactory.getLogger(this.getClass)

  def findGameByLiveMatch(liveMatch: LiveMatch): Future[ScheduledGame] = {

    val gamesFuture = repository.getScheduledGames(liveMatch.radiantTeamBoard.team.id, liveMatch.direTeamBoard.team.id, liveMatch.leagueId, liveMatch.seriesType)
    val hoursForTheGame = TimeUnit.HOURS.toMillis(liveMatch.seriesType.gamesCount + 1)
    val game = gamesFuture.map(future =>
      future.find(game =>
        (System.currentTimeMillis() - game.startDate.getTime) < hoursForTheGame))
    val scheduledGame = game.map(g =>
      if (g.isDefined) {
        if (MatchStatus.FINISHED.status == g.get.status) {
          val copy = g.get.copy(status = MatchStatus.LIVE.status)
          repository.update(copy)
          copy
        } else {
          g.get
        }
      } else {
        val newGame = ScheduledGame(-1, liveMatch.radiantTeamBoard.team.id, liveMatch.direTeamBoard.team.id, liveMatch.leagueId, liveMatch.seriesType.code, new Timestamp(System.currentTimeMillis() - liveMatch.duration.toLong * 1000))
        val newGameId = Await.result(repository.insertAndGet(newGame), timeout)
        newGame.copy(id = newGameId)
      })
    scheduledGame
  }

  def finishTheGame(gameId: Int) = {
    repository.updateStatus(gameId, MatchStatus.FINISHED.status)
  }

  def getRecentGames(millis: Long): Future[Map[ScheduledGame, Seq[MatchSeries]]] = {
    val c = Calendar.getInstance()
    c.setTimeInMillis(millis)
    c.add(Calendar.HOUR_OF_DAY, -24)
    val start = c.getTimeInMillis
    val end = millis + TimeUnit.SECONDS.toMillis(10)


    val gamesByDate: Future[Seq[(ScheduledGame, MatchSeries)]] = repository.getGamesBetweenDateRethink(new Timestamp(start), new Timestamp(end))
    val map: Future[Map[ScheduledGame, Seq[MatchSeries]]] = gamesByDate.map(seq => seq.groupBy(_._1).mapValues(_.map(_._2)))
    map.onFailure {
      case ex => log.error("couldn't get games between dates " + new Date(millis), ex)
    }
    map
  }
}
