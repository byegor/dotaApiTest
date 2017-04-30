package com.eb.pulse.crawler.service

import java.sql.Timestamp

import com.eb.pulse.crawler.TestLookup
import com.eb.schedule.model.BasicWordSuiteTest
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}

/**
  * Created by Egor on 22.04.2017.
  */
class MatchServiceTest extends BasicWordSuiteTest {


  "Finish live match" should {
    "finish current match" in {
      val scheduledGame = ScheduledGame(-1, 35, 34, 1, 0)
      whenReady(TestLookup.scheduledGameRepository.insertAndGet(scheduledGame)) {
        gameId =>
          val matchSeries = MatchSeries(scheduledGameId = gameId, matchId = 1, gameNumber = 1, radiantWin = None, finished = false, radiantTeam = 35, new Timestamp(0))
          whenReady(TestLookup.seriesRepository.insert(matchSeries)) {
            res =>
              whenReady(TestLookup.matchService.finishMatch(1)) {
                r =>
                  whenReady(TestLookup.seriesRepository.findSeriesByGameId(gameId)) {
                    seq =>
                      val stored = seq.head
                      assert(1 == stored.matchId, "match id should be equal")
                      assert(gameId == stored.scheduledGameId)
                      assert(stored.finished, "Match should be marked as finished")
                  }
              }
          }
      }
    }
  }
}
