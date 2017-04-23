package com.eb.pulse.crawler.service

import java.sql.Timestamp

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.model.{LiveMatch, TeamScoreBoard}
import com.eb.schedule.model.slick.{ScheduledGame, Team}
import com.eb.schedule.model.{BasicTest, MatchStatus, SeriesType}

/**
  * Created by Egor on 22.04.2017.
  */
class GameServiceTest extends BasicTest with Lookup {

  test("testFindGameByLiveMatch") {
    val lm = LiveMatch(1, -1, TeamScoreBoard(Team(1)), TeamScoreBoard(Team(2)), 1, 0, 1, 0, 0, SeriesType.BO1, 0, 0)

    whenReady(scheduledGameRepository.insert(ScheduledGame(1, 2, 1, 1, 0, new Timestamp(1), 0))) {
      res =>
        whenReady(gameService.findGameByLiveMatch(lm)) {
          game =>
            assert(1 == game.id, "should find previously inserted scheduled game")
            assert(MatchStatus.LIVE.status == game.status, "Status should be updated")
        }
    }
  }

  test("testFind non existing Game By Live Match:") {
    val lm = LiveMatch(1, -1, TeamScoreBoard(Team(1)), TeamScoreBoard(Team(2)), 2, 0, 1, 0, 0, SeriesType.BO1, 0, 0)

    whenReady(scheduledGameRepository.insert(ScheduledGame(1, 2, 1, 1, 0, new Timestamp(1), 0))) {
      res =>
        whenReady(gameService.findGameByLiveMatch(lm)) {
          game =>
            assert(2 == game.id, "should create new scheduled game as league id not the same")
            assert(MatchStatus.LIVE.status == game.status, "Status by default should be LIVE")
        }
    }

  }

}
