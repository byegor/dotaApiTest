package com.eb.pulse.crawler.data.task

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.TestLookup
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.model.{BasicWordSuiteTest, MatchStatus}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Iegor.Bondarenko on 04.05.2017.
  */
class FindLongRunningGameTaskTest extends BasicWordSuiteTest {


  "Old running game" should {
    "marked as finished if all matches are have finished=true and two hours passed from the newest match" in {
      val task = new FindLongRunningGameTask(TestLookup.gameService, TestLookup.matchService, null)

      whenReady(TestLookup.scheduledGameRepository.insertAndGet(ScheduledGame(-1, 1, 2, 1, 0, status = MatchStatus.LIVE.status))) {
        gameId =>
          whenReady(Future {
            TestLookup.seriesRepository.insert(MatchSeries(gameId, 1, 1, Some(true), finished = true, 1, new Timestamp(123213)))
            TestLookup.seriesRepository.insert(MatchSeries(gameId, 2, 2, Some(true),finished =  true, 1, new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3))))
            task.run()
          }) {
            ignore =>
              whenReady(TestLookup.scheduledGameRepository.findById(gameId)) {
                game =>
                  game.status shouldEqual MatchStatus.FINISHED.status
              }
          }

      }
    }

    "not be marked as finished even if there is still running matches according to db but the last match should be marked as finished" in {
      val task = new FindLongRunningGameTask(TestLookup.gameService, TestLookup.matchService, null) {
        override def isFinished(matchId: Long): Boolean = true
      }
      whenReady(TestLookup.scheduledGameRepository.insertAndGet(ScheduledGame(-1, 1, 2, 1, 0, status = MatchStatus.LIVE.status))) {
        gameId =>
          whenReady(Future {
            TestLookup.seriesRepository.insert(MatchSeries(gameId, 1, 1, Some(true), finished = true, 1, new Timestamp(123213)))
            TestLookup.seriesRepository.insert(MatchSeries(gameId, 2, 2, None, finished = false, 1, new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3))))
            task.run()
          }) {
            ignore =>
              whenReady(TestLookup.scheduledGameRepository.findById(gameId)) {
                game =>
                  game.status shouldEqual MatchStatus.LIVE.status
              }

              whenReady(TestLookup.seriesRepository.findByMatchId(2)) {
                matchOpt =>
                  matchOpt shouldBe defined
                  matchOpt.get.finished shouldEqual true
              }
          }

      }

    }
  }
}