package com.eb.pulse.crawler.service

import java.sql.Timestamp

import com.eb.pulse.crawler.model.LiveMatch
import com.eb.schedule.dao.SeriesRepository
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 22.04.2017.
  */
class MatchService(seriesRepository: SeriesRepository) extends Service {

  def insertNewMatch(liveMatch: LiveMatch, gameId: Int, radiantTeamId: Int) = {
    seriesRepository.exists(gameId, liveMatch.matchId).onSuccess {
      case exists =>
        val matchSeries = new MatchSeries(gameId, liveMatch.matchId, (liveMatch.winByRadiant + liveMatch.winByDire + 1).toByte,
          None, false, radiantTeamId, new Timestamp(System.currentTimeMillis() - liveMatch.duration.toLong * 1000))
        if (exists) {
          seriesRepository.update(matchSeries)
        } else {
          seriesRepository.insert(matchSeries)
        }
    }
  }

  def finishMatch(matchId:Long) ={
    seriesRepository.update(matchId, true)
  }

  def getUnfinishedSeries(): Future[Map[ScheduledGame, Seq[MatchSeries]]] = {
    val unfinishedMatchesFuture = seriesRepository.getUnfinishedSeries
    unfinishedMatchesFuture.map(result => result.groupBy(_._1).mapValues(_.map(_._2)))
  }

  def updateMatchWithWinner(matchId: Long, radiantWin: Boolean): Unit ={
    seriesRepository.updateMatchWithRadiantWin(matchId, Some(radiantWin))
  }
}
