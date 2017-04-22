package com.eb.pulse.crawler.service

import java.sql.Timestamp

import com.eb.pulse.crawler.model.LiveMatch
import com.eb.schedule.dao.SeriesRepository
import com.eb.schedule.model.slick.MatchSeries

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

  def finishMatch(matchId:Long): Unit ={
    seriesRepository.update(matchId, true)
  }
}
