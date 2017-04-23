package com.eb.pulse.crawler.service

import java.sql.Timestamp

import com.eb.pulse.crawler.Lookup
import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.slick.MatchSeries

/**
  * Created by Egor on 22.04.2017.
  */
class MatchServiceTest extends BasicTest with Lookup {

  test("testFinishMatch") {
    val matchSeries = MatchSeries(2, 1, 1, None, false, 1, new Timestamp(0))
    whenReady(seriesRepository.insert(matchSeries)) {
      res =>
        whenReady(matchService.finishMatch(1)) {
          r =>
            whenReady(seriesRepository.findSeriesId(1)) {
              seq =>
                val stored = seq.head
                assert(1 == stored.matchId)
                assert(2 == stored.scheduledGameId)
                assert(stored.finished)
            }
        }
    }
  }

  /*test("testInsertNewMatch via insert") {
    val matchSeries = MatchSeries(2, 1, 1, None, false, 1, new Timestamp(0))
    whenReady(seriesRepository.insert(matchSeries)) {
      res =>
        whenReady(matchService.insertNewMatch(1)) {
          r =>
            whenReady(seriesRepository.findSeriesId(1)) {
              seq =>
                val stored = seq.head
                assert(1 == stored.matchId)
                assert(2 == stored.scheduledGameId)
                assert(stored.finished)
            }
        }
    }
  }*/

}
