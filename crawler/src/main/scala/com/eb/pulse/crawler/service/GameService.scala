package com.eb.pulse.crawler.service

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.model.LiveMatch
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.dao.ScheduledGameRepository
import com.eb.schedule.model.slick.ScheduledGame

import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 21.04.2017.
  */
class GameService(repository: ScheduledGameRepository) extends Service {

  def findGameByLiveMatch(liveMatch: LiveMatch): Future[ScheduledGame] = {

    val gamesFuture = repository.getScheduledGames(liveMatch.radiantTeamBoard.team.id, liveMatch.direTeamBoard.team.id, liveMatch.league, liveMatch.seriesType)
    val hoursForTheGame = TimeUnit.HOURS.toMillis(liveMatch.seriesType.gamesCount + 1)
    val game = gamesFuture.map(future => future.find(game => (System.currentTimeMillis() - game.startDate.getTime) < hoursForTheGame))
    val scheduledGame = game.map(g => if (g.isDefined) {
      if (MatchStatus.FINISHED.status == g.get.status) {
        val copy = g.get.copy(status = MatchStatus.LIVE.status)
        repository.update(copy)
        copy
      } else {
        g.get
      }
    } else {
      val newGame = ScheduledGame(-1, liveMatch.radiantTeamBoard.team.id, liveMatch.direTeamBoard.team.id, liveMatch.league, liveMatch.seriesType.code, new Timestamp(System.currentTimeMillis() - liveMatch.duration.toLong * 1000))
      val newGameId = Await.result(repository.insertAndGet(newGame), timeout)
      newGame.copy(id = newGameId)
    })
    scheduledGame
  }
}
