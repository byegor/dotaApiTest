package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.TestLookup
import com.eb.pulse.crawler.model.{LiveMatch, Player}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.eb.schedule.model.{BasicWordSuiteTest, MatchStatus, SeriesType}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.BufferedSource


/**
  * Created by Iegor.Bondarenko on 30.04.2017.
  */
class LiveMatchTaskIntegrationTest extends BasicWordSuiteTest {

  val mockedHttpUtils = new HttpUtils() {
    override def getResponseAsJson(url: String): JsonObject = {
      val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
      val lines = try source.mkString finally source.close()
      new JsonParser().parse(lines).getAsJsonObject
    }

    override def sendData(data: String) = {}
  }
  val task = new LiveMatchTask(TestLookup.gameService, TestLookup.matchService, mockedHttpUtils, TestLookup.netWorthService, TestLookup.cacheHelper, TestLookup.teamService)

  "LiveMatchTask.run()" should {

    "create scheduled game" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          val allGames = TestLookup.scheduledGameRepository.findAll()
          whenReady(allGames) {
            gamesSeq =>
              gamesSeq.size shouldEqual 1 withClue " live match was only one and new so new game should be created"
              val game = gamesSeq.head
              game.radiant shouldEqual 2593210
              game.dire shouldEqual 2312626
              game.leagueId shouldEqual 4446
              SeriesType.fromCode(game.seriesType) shouldEqual SeriesType.BO3
              MatchStatus.fromValue(game.status) shouldEqual MatchStatus.LIVE
          }
      }
    }

    "create match" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          whenReady(TestLookup.seriesRepository.findByMatchId(2215228651l)) {
            matchOpt =>
              matchOpt shouldBe defined
              val storedMatch = matchOpt.get
              storedMatch.gameNumber shouldEqual 2
              storedMatch.finished shouldEqual false
              storedMatch.radiantWin should not be defined
              storedMatch.radiantTeam shouldEqual 2593210
          }
      }
    }

    "create radiant and dire teams" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          whenReady(TestLookup.teamService.findByTeamId(2593210)) {
            radiant =>
              radiant shouldBe defined
              radiant.get shouldEqual Team(2593210, "44HARDCORE ESPORTS", "", 396675411355453900l)
          }

          whenReady(TestLookup.teamService.findByTeamId(2312626)) {
            dire =>
              dire shouldBe defined
              dire.get shouldEqual Team(2312626, "-Do4a", "", 32991979777436732l)
          }
      }
    }

    "create league task" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          whenReady(TestLookup.taskService.getPendingLeagueTasks()) {
            leagueTaskSeq =>
              leagueTaskSeq.size shouldEqual 1
              leagueTaskSeq.head shouldEqual UpdateTask(4446, "League$", 0)
          }
      }
    }

    "insert overall networth" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          val netWorth = TestLookup.netWorthService.findByMatchId(2215228651l)
          val value = netWorth.netWorth
          value should not be empty
          value.split(",").length shouldEqual 1
          value shouldEqual "549"
      }
    }

    "cache teams" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          val radiant = TestLookup.teamCache.getTeam(2593210)
          radiant shouldEqual Team(2593210, "44HARDCORE ESPORTS", "", 396675411355453900l)

          val dire = TestLookup.teamCache.getTeam(2312626)
          dire shouldEqual Team(2312626, "-Do4a", "", 32991979777436732l)
      }
    }

    "cache player names" in {
      whenReady(Future {
        task.run()
      }) {
        ignore =>
          val cache = TestLookup.playerCache
          cache.getPlayerName(126931811) shouldEqual "TEHb"
          cache.getPlayerName(86446727) shouldEqual "Suyoko"
          cache.getPlayerName(303091343) shouldEqual "This is My City"
          cache.getPlayerName(150834698) shouldEqual "Maniac^"
          cache.getPlayerName(103112067) shouldEqual "JORA_INVALID"
      }
    }
  }

  "send appropriate match bean" in {
    val liveTask = new LiveMatchTask(TestLookup.gameService, TestLookup.matchService, mockedHttpUtils, TestLookup.netWorthService, TestLookup.cacheHelper, TestLookup.teamService) {
      override def sendMatches(liveMatchesSeq: Seq[LiveMatch]): Unit = {
        liveMatchesSeq.size shouldEqual 1
        val liveMatch = liveMatchesSeq.head
        liveMatch.matchId shouldEqual 2215228651l
        liveMatch.scheduledGameId should be > 0
        liveMatch.leagueId shouldEqual 4446
        liveMatch.currentNet shouldEqual 549
        liveMatch.duration.toInt shouldEqual 129
        liveMatch.radiantScore shouldEqual 3
        liveMatch.direScore shouldEqual 2
        liveMatch.winByRadiant shouldEqual 1
        liveMatch.winByDire shouldEqual 0

        val radiantBoard = liveMatch.radiantTeamBoard
        radiantBoard.team shouldEqual Team(2593210, "44HARDCORE ESPORTS", "", 396675411355453900l)
        radiantBoard.players.size shouldEqual 5
        radiantBoard.players.last shouldEqual Player(126931811, "TEHb", 92, List(27, 0, 38, 237, 29, 46), 5, 0, 0, 1, 1454)
        radiantBoard.picks shouldEqual List(41, 30, 70, 92, 13)
        radiantBoard.bans shouldEqual List(43, 74, 72, 18, 47)
        radiantBoard.score shouldEqual 3
      }
    }

    whenReady(Future{liveTask.run()}){
      ignore=>
    }
  }
}
