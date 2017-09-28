package com.eb.pulse.crawler.data.task

import java.sql.Timestamp

import com.eb.pulse.crawler.TestLookup
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.eb.schedule.model.{BasicFunSuiteTest, MatchStatus, SeriesType}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.BufferedSource

/**
  * Created by Egor on 14.05.2016.
  */
class FindWinnerForTheMatchTaskTest extends BasicFunSuiteTest{


  val winnerCrawler = new FindWinnerForTheMatchTask(TestLookup.matchService, new HttpUtils() {
    override def getResponseAsJson(url: String): JsonObject = {
      val source: BufferedSource = io.Source.fromURL(getClass.getResource("/match.json"))
      val lines = try source.mkString finally source.close()
      new JsonParser().parse(lines).getAsJsonObject
    }
  })


  test("testUpdateWinner") {
    var id: Int = -1
    val precondition: Future[Unit] = Future {
      val game: ScheduledGame = ScheduledGame(-1, 36, 1838315, 4210, SeriesType.BO3.code, new Timestamp(0), MatchStatus.LIVE.status)
      val gameId = TestLookup.scheduledGameRepository.insertAndGet(game)
      gameId.onSuccess {
        case id =>
          TestLookup.seriesRepository.insert(MatchSeries(id, 1l, 1, None, true, 36, new Timestamp(0)))
          TestLookup.seriesRepository.insert(MatchSeries(id, 2l, 2, None, false, 36, new Timestamp(1)))
      }
    }

    whenReady(precondition) { result =>
      whenReady(TestLookup.seriesRepository.getSeriesWithoutWinner()) { result =>
        assert(1 == result.size)
      }
      whenReady(Future {
        winnerCrawler.run()
      }) { res =>
        whenReady(TestLookup.seriesRepository.getSeriesWithoutWinner()) { result =>
          assert(0 == result.size)
        }
      }
    }
  }
}
