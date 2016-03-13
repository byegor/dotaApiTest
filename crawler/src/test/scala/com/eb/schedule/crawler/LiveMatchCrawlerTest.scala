package com.eb.schedule.crawler

import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.slick.{Pick, LiveGame, Team, UpdateTask}
import org.json.{JSONArray, JSONObject}
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.BufferedSource

/**
  * Created by Egor on 11.03.2016.
  */
class LiveMatchCrawlerTest extends BasicTest {

  val crawler = new LiveMatchCrawler(teamService, leagueService, liveGameService, pickService, scheduledGameService)

  test("extract Live game") {
    val gameInfo: (LiveGame, List[Pick], Int, Int) = crawler.extractGameInfo(liveMatchJson)
    equals(liveGame, gameInfo._1)
  }

  test("extract Picks") {
    val gameInfo: (LiveGame, List[Pick], Int, Int) = crawler.extractGameInfo(liveMatchJson)
    val gamePicks = List(
      //radiant pick
      Pick(2215228651l, 30, true, true),
      Pick(2215228651l, 70, true, true),
      Pick(2215228651l, 92, true, true),
      Pick(2215228651l, 41, true, true),
      Pick(2215228651l, 13, true, true),
      //radiant ban
      Pick(2215228651l, 43, true, false),
      Pick(2215228651l, 74, true, false),
      Pick(2215228651l, 72, true, false),
      Pick(2215228651l, 18, true, false),
      Pick(2215228651l, 47, true, false),
      //dire pick
      Pick(2215228651l, 20, false, true),
      Pick(2215228651l, 29, false, true),
      Pick(2215228651l, 6, false, true),
      Pick(2215228651l, 66, false, true),
      Pick(2215228651l, 46, false, true),
      //dire ban
      Pick(2215228651l, 80, false, false),
      Pick(2215228651l, 76, false, false),
      Pick(2215228651l, 39, false, false),
      Pick(2215228651l, 67, false, false),
      Pick(2215228651l, 22, false, false)
    ).sortBy(_.heroId)

    assert(gamePicks == gameInfo._2.sortBy(_.heroId))
  }

  test("extract score board") {
    val gameInfo: (LiveGame, List[Pick], Int, Int) = crawler.extractGameInfo(liveMatchJson)
    assert(3 == gameInfo._3, "score for radiant was parsed wrong")
    assert(2 == gameInfo._4, "score for dire was parsed wrong")
  }

  test("store new live game"){
    val game = new LiveGame(1l, 2, 3, 4, 1, new java.sql.Timestamp(529l), 0, 0)
    crawler.saveGameInfo((game, Nil, 1,1))
    var cnt = 0
    while(!Await.result(liveGameService.exists(1), Duration.Inf) && cnt < 10){
      cnt = cnt + 1
      Thread.sleep(300)
    }

    assert(Await.result(teamService.exists(2), Duration.Inf), "radiant team not saved")
    assert(Await.result(teamService.exists(3), Duration.Inf), "dire team not saved")
    assert(Await.result(leagueService.exists(4), Duration.Inf), "league not saved")
    assert(Await.result(liveGameService.exists(1), Duration.Inf), "live game not saved")
    assert(Await.result(scheduledGameService.findByMatchId(1l), Duration.Inf) != null, "scheduled game not saved")

  }


  def getLiveMatchResponse: JSONObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
    val lines = try source.mkString finally source.close()
    val liveMatchResult: JSONObject = new JSONObject(lines)
    liveMatchResult
  }


  val liveGame = new LiveGame(2215228651l, 2593210, 2312626, 4446, 1, new java.sql.Timestamp(529l), 1, 2)
  val liveMatchJson: JSONObject = {
    val response: JSONObject = getLiveMatchResponse
    val gamesList: JSONArray = response.getJSONObject("result").getJSONArray("games")
    val nObject: JSONObject = gamesList.getJSONObject(0)
    nObject
  }

  def equals(l1: LiveGame, l2: LiveGame) {
    l1.startDate = l2.startDate
    assert(l1 == l2)
  }


}
