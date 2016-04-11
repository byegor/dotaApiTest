package com.eb.schedule.live

import com.eb.schedule.{HttpUtilsMock, RestBasicTest}
import com.eb.schedule.dto.ScheduledGameDTO
import com.eb.schedule.model.MatchStatus
import org.json.JSONObject
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.BufferedSource

/**
  * Created by Egor on 23.03.2016.
  */
class LiveGameProcessorTest extends RestBasicTest {

  val processor: LiveGameProcessor = createProcessor
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

  test("scheduled game"){

  }

}
