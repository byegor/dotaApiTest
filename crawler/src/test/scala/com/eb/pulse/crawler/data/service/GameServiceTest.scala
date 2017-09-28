package com.eb.pulse.crawler.data.service

import com.eb.pulse.crawler.TestLookup
import com.eb.pulse.crawler.data.model.{LiveMatch, TeamScoreBoard}
import com.eb.schedule.model.slick.{ScheduledGame, Team}
import com.eb.schedule.model.{BasicWordSuiteTest, MatchStatus, SeriesType}

/**
  * Created by Egor on 22.04.2017.
  */
class GameServiceTest extends BasicWordSuiteTest{

  "Find Scheduled game by Live Match" should {
    "find game that already was there" in {
      val lm = LiveMatch(1, -1, TeamScoreBoard(Team(1)), TeamScoreBoard(Team(2)), 1, 0, 1, SeriesType.BO1, 0, 0)

      whenReady(TestLookup.scheduledGameRepository.insertAndGet(ScheduledGame(1, 1, 2, 1, 0, status = MatchStatus.FINISHED.status))) {
        gameId =>
          whenReady(TestLookup.gameService.findGameByLiveMatch(lm)) {
            game =>
              assert(gameId == game.id, "should find previously inserted scheduled game")
              assert(MatchStatus.LIVE.status == game.status, "Status should be updated")
          }
      }
    }

    "create new game as league id doesn't match" in {
      val lm = LiveMatch(1, -1, TeamScoreBoard(Team(1)), TeamScoreBoard(Team(2)), 2, 0, 1, SeriesType.BO1, 0, 0)

      whenReady(TestLookup.scheduledGameRepository.insertAndGet(ScheduledGame(id = 1, radiant = 1, dire = 2, leagueId = 1, SeriesType.BO1.code, status = 0))) {
        gameId =>
          whenReady(TestLookup.gameService.findGameByLiveMatch(lm)) {
            game =>
              assert(gameId + 1 == game.id, "should create new scheduled game as league id not the same")
              assert(MatchStatus.LIVE.status == game.status, "Status by default should be LIVE")
          }
      }

    }
  }

}
