package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto.{LeagueDTO, ScheduledGameDTO, SeriesDTO, TeamDTO}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.{HttpUtilsMock, RestBasicTest}
import org.json.{JSONArray, JSONObject}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 23.03.2016.
  */
class LiveGameProcessorTest extends RestBasicTest {

  val processor: LiveGameProcessor = createProcessor()
  val MATCH_ID: Long = 2234857740l

  private def createProcessor(): LiveGameProcessor = {
    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, new HttpUtilsMock)
  }

  test("getLiveLeagueGames") {
    val games: List[JSONObject] = processor.getLiveLeagueGames()
    assert(1 == games.size)
  }

  test("get absoluteNewGame") {
    processor.process()
    var cnt = 0
    var scheduledGameDTO: Option[ScheduledGameDTO] = None
    while (cnt < 4) {
      scheduledGameDTO = Await.result(scheduledGameService.findByMatchId(MATCH_ID), Duration.Inf)
      if (scheduledGameDTO.isDefined) {
        cnt = 5
      } else {
        Thread.sleep(3000)
        cnt = cnt + 1
      }
    }
    assert(scheduledGameDTO.isDefined, "failed to store new scheduled game")
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "status of the game is wrong")
    assert(LiveGameContainer.exists(MATCH_ID))
  }

  test("scheduled game") {
    Await.result(scheduledGameService.insert(new ScheduledGameDTO(-1, None, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), new Timestamp(1l), MatchStatus.SCHEDULED)), Duration.Inf)
    LiveGameContainer.removeLiveGame(MATCH_ID)
    processor.process()
    var cnt = 0
    var scheduledGameDTO: Option[ScheduledGameDTO] = None
    while (cnt < 4) {
      scheduledGameDTO = Await.result(scheduledGameService.findByMatchId(MATCH_ID), Duration.Inf)
      if (scheduledGameDTO.isDefined) {
        cnt = 6
      } else {
        Thread.sleep(3000)
        cnt = cnt + 1
      }
    }
    assert(scheduledGameDTO.isDefined, "seems it couldn't find scheduled game by live game")
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "failed to set LIVE status")
  }

  test("finish match") {
    LiveGameContainer.removeLiveGame(MATCH_ID)
    processor.process()
    Thread.sleep(3000)
    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, new HttpUtilsMock() {
      override def getResponseAsJson(url: String): JSONObject = {
        val json: JSONObject = new JSONObject()
        val array: JSONArray = new JSONArray()
        json.put("games", array)
        val res: JSONObject = new JSONObject()
        res.put("result", json)
        res
      }
    }).process()
    Thread.sleep(3000)
    assert(!LiveGameContainer.exists(MATCH_ID))
    val gameOpt: Option[ScheduledGameDTO] = Await.result(scheduledGameService.findByMatchId(MATCH_ID), Duration.Inf)
    assert(gameOpt.isDefined)
    assert(gameOpt.get.matchStatus == MatchStatus.FINISHED)
    val series: Seq[SeriesDTO] = Await.result(seriesService.findBySeriesId(gameOpt.get.id), Duration.Inf)
    assert(series.size == 1)
    assert(series.head.gameNumber == 3)
  }

}
