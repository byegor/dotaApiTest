package com.eb.schedule.crawler

import java.sql.Timestamp

import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.{BasicTest, MatchStatus, SeriesType}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.BufferedSource
import scala.util.{Failure, Success}

/**
  * Created by Egor on 14.05.2016.
  */
class WinnerCrawlerTest extends BasicTest {

  val winnerCrawler = new WinnerCrawler(seriesService, scheduledGameService, new HttpUtils() {
    override def getResponseAsJson(url: String): JsonObject = {
      val source: BufferedSource = io.Source.fromURL(getClass.getResource("/match.json"))
      val lines = try source.mkString finally source.close()
      new JsonParser().parse(lines).getAsJsonObject
    }
  })

  test("testUpdateWinner") {
    var id: Int = -1
    val precondition: Future[Unit] = Future {
      val game: ScheduledGameDTO = new ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), SeriesType.BO3, new Timestamp(0), MatchStatus.LIVE)
      val gameId: Future[Int] = scheduledGameService.insertAndGet(game)
      gameId.onComplete {
        case Success(gameId) =>
          seriesService.insert(new SeriesDTO(gameId, 1l, 1, None, true))
          seriesService.insert(new SeriesDTO(gameId, 2l, 2, None, false))
          id = gameId
        case Failure(e) => throw e;
      }
    }

    whenReady(precondition) { result =>
      whenReady(seriesService.getSeriesWithoutWinner()) { result =>
        assert(1 == result.size)
      }
      whenReady(Future {
        winnerCrawler.run()
      }) { res =>
        whenReady(seriesService.getSeriesWithoutWinner()) { result =>
          assert(0 == result.size)
        }
      }
    }
  }


}
