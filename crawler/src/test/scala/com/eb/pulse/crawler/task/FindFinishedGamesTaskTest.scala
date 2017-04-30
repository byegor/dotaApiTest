package com.eb.pulse.crawler.task

import java.sql.Timestamp

import com.eb.pulse.crawler.TestLookup
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.model.{BasicFunSuiteTest, MatchStatus, SeriesType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 14.05.2016.
  */
class FindFinishedGamesTaskTest extends BasicFunSuiteTest {

  val seriesCrawler = new FindFinishedGamesTask(TestLookup.gameService, TestLookup.matchService)

  test("testUpdateStatus for not finished series") {
    var scheduledGameId: Int = -1
    val precondition = Future {
      val game: ScheduledGame = ScheduledGame(1, 36, 1838315, 4210, SeriesType.BO3.code, new Timestamp(0), MatchStatus.LIVE.status)
      val gameId = TestLookup.scheduledGameRepository.insertAndGet(game)
      gameId.onSuccess {
        case id =>
          TestLookup.seriesRepository.insert(MatchSeries(id, 1l, 1, Some(true), true, 36, new Timestamp(0)))
          scheduledGameId = id
      }
    }

    whenReady(precondition) { result =>
      seriesCrawler.run()
    }

    val unfinishedSeries: Future[Map[ScheduledGame, Seq[MatchSeries]]] = TestLookup.matchService.getUnfinishedSeries()
    whenReady(unfinishedSeries) {
      res => {
        assert(1 == res.size)
        assert(scheduledGameId == res.values.head.head.scheduledGameId)
      }
    }
  }


  test("testUpdateStatus for finished series ") {
    var scheduledGameId: Int = -1
    val precondition: Future[Unit] = Future {
      val game: ScheduledGame = ScheduledGame(1, 36, 1838315, 4210, SeriesType.BO3.code, new Timestamp(0), MatchStatus.LIVE.status)
      val gameId = TestLookup.scheduledGameRepository.insertAndGet(game)
      gameId.onSuccess {
        case id =>
          TestLookup.seriesRepository.insert(MatchSeries(id, 1l, 1, Some(true), true, 36, new Timestamp(0)))
          TestLookup.seriesRepository.insert(MatchSeries(id, 2l, 2, Some(false), true, 36, new Timestamp(0)))
          TestLookup.seriesRepository.insert(MatchSeries(id, 3l, 3, Some(false), true, 36, new Timestamp(0)))
          scheduledGameId = id
      }
    }

    whenReady(precondition) { result =>
      seriesCrawler.run()
    }
    whenReady(TestLookup.matchService.getUnfinishedSeries()) {
      res => assert(0 == res.size)
    }

    whenReady(TestLookup.scheduledGameRepository.findById(scheduledGameId)) {
      result =>
        assert(MatchStatus.FINISHED.status == result.status)
    }
  }
}
