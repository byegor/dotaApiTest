package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.model.{MatchStatus, SeriesType}
import com.eb.schedule.{HttpUtilsMock, RestBasicTest}
import com.google.gson.{JsonArray, JsonObject}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 23.03.2016.
  */
class LiveGameProcessorTest extends RestBasicTest {

  val processor: LiveGameProcessor = createProcessor()
  val MATCH_ID: Long = 2234857740l

  private def createProcessor(): LiveGameProcessor = {
    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, taskService, new HttpUtilsMock)
  }

  test("getLiveLeagueGames") {
    val games: List[JsonObject] = processor.getLiveLeagueGames()
    assert(1 == games.size)
  }

  test("get absoluteNewGame") {
    Future {
      processor.run()
    }.futureValue
    val scheduledGameDTO = scheduledGameService.getScheduledGames(GameContainer.getLiveGame(MATCH_ID).get, MatchStatus.LIVE)
    whenReady(seriesService.findBySeriesId(scheduledGameDTO.get.id)) {
      seq =>
        assert(1 == seq.size)
        assert(!seq.head.finished, "game just started and couldn't be finished")
    }
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "status of the game is wrong")
    assert(GameContainer.exists(MATCH_ID))
  }

  test("scheduled game") {
    Await.result(scheduledGameService.insert(new ScheduledGameDTO(-1, new TeamDTO(36), new TeamDTO(1838315), new LeagueDTO(4210), SeriesType.BO3, new Timestamp(1l), MatchStatus.SCHEDULED)), Duration.Inf)
    GameContainer.removeLiveGame(MATCH_ID)
    Future {
      processor.run()
    }.futureValue

    val scheduledGameDTO = scheduledGameService.getScheduledGames(GameContainer.getLiveGame(MATCH_ID).get, MatchStatus.LIVE)
    assert(scheduledGameDTO.isDefined, "seems it couldn't find scheduled game by live game")
    assert(scheduledGameDTO.get.matchStatus == MatchStatus.LIVE, "failed to set LIVE status")
  }

  test("finish match") {
    GameContainer.removeLiveGame(MATCH_ID)
    Future {
      processor.run()
    }.futureValue

    val currentMatch: CurrentGameDTO = GameContainer.getLiveGame(MATCH_ID).get

    val emptyProcessor = new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, taskService, new HttpUtilsMock() {
      override def getResponseAsJson(url: String): JsonObject = {
        val json: JsonObject = new JsonObject()
        val array: JsonArray = new JsonArray()
        json.add("games", array)
        val res: JsonObject = new JsonObject()
        res.add("result", json)
        res
      }
    })
    Future {
      emptyProcessor.run()
    }.futureValue

    Future {
      emptyProcessor.run()
    }.futureValue


    assert(!GameContainer.exists(MATCH_ID))
    val gameOpt: Option[ScheduledGameDTO] = scheduledGameService.getScheduledGames(currentMatch, MatchStatus.LIVE)
    assert(gameOpt.isDefined, "it is not the last game, so should be live status")

    new LiveGameProcessor(liveGameHelper, netWorthService, scheduledGameService, seriesService, taskService, new HttpUtilsMock() {
      override def getResponseAsJson(url: String): JsonObject = {
        val json: JsonObject = new JsonObject()
        val array: JsonArray = new JsonArray()
        array.add(getGame())
        json.add("games", array)
        val res: JsonObject = new JsonObject()
        res.add("result", json)
        res
      }
    }).run()
    Future {
      emptyProcessor.run()
    }.futureValue
    Future {
      emptyProcessor.run()
    }.futureValue
    assert(!GameContainer.exists(2234857741l))
    val finishedMatch: Option[ScheduledGameDTO] = scheduledGameService.getScheduledGames(currentMatch)
    assert(finishedMatch.isDefined)
    assert(MatchStatus.LIVE == finishedMatch.get.matchStatus)
    whenReady(seriesService.findBySeriesId(finishedMatch.get.id)) {
      seq =>
        assert(2 == seq.size)
        seq.foreach(series => assert(series.finished && series.radiantWin.isEmpty))
    }
  }

}
