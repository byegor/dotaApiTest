package com.eb.schedule.crawler

import java.sql.Timestamp

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.task.FindFinishedGamesTask
import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.{BasicTest, MatchStatus, SeriesType}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.BufferedSource

/**
  * Created by Egor on 14.05.2016.
  */
class SeriesCrawlerTest extends BasicTest  with Lookup{

  val seriesCrawler = new FindFinishedGamesTask()

  test("testUpdateStatus for not finished series") {
    var id: Int = -1
    val precondition: Future[Unit] = Future {
      val game: ScheduledGameDTO = new ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), SeriesType.BO3, new Timestamp(0), MatchStatus.LIVE)
      val gameId = scheduledGameService.insertAndGet(game)
      seriesService.insert(new SeriesDTO(gameId, 1l, 1, Some(true), true, 36));
      id = gameId
    }

    whenReady(precondition) { result =>
      seriesCrawler.run()
    }

    val unfinishedSeries: Map[ScheduledGameDTO, Seq[SeriesDTO]] = seriesService.getUnfinishedSeries()
    assert(1 == unfinishedSeries.size)
    assert(id == unfinishedSeries.values.head.head.gameId)
  }

  test("testUpdateStatus for finished series ") {
    var id: Int = -1
    val precondition: Future[Unit] = Future {
      val game: ScheduledGameDTO = new ScheduledGameDTO(1, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), SeriesType.BO3, new Timestamp(0), MatchStatus.LIVE)
      val gameId: Int = scheduledGameService.insertAndGet(game)
      seriesService.insert(new SeriesDTO(gameId, 1l, 1, Some(true), true, 36))
      seriesService.insert(new SeriesDTO(gameId, 2l, 2, Some(false), true, 36))
      seriesService.insert(new SeriesDTO(gameId, 3l, 3, Some(false), true, 36))
      id = gameId
    }

    whenReady(precondition) { result =>
      seriesCrawler.run()
    }
    assert(0 == seriesService.getUnfinishedSeries().size)

    whenReady(scheduledGameService.findById(id)) {
      result =>
        assert(MatchStatus.FINISHED == result.matchStatus)
    }
  }
}
