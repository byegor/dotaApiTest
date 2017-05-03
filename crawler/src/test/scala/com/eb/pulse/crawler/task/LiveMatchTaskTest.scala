package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.TestLookup
import com.eb.pulse.crawler.data.GameDataHolder
import com.eb.pulse.crawler.model.{LiveMatch, Player, TeamScoreBoard}
import com.eb.schedule.model.slick.Team
import com.eb.schedule.model.{BasicWordSuiteTest, SeriesType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by Iegor.Bondarenko on 30.04.2017.
  */
class LiveMatchTaskTest extends BasicWordSuiteTest {

  val liveMatchTask = new LiveMatchTask(TestLookup.gameService, TestLookup.matchService, TestLookup.httpUtils, TestLookup.netWorthService, TestLookup.cacheHelper)

  "LiveMatchTaskTest" should {

    "filterOutLessPlayers - filter out matches with less then 5 players per team" in {
      val liveMatch = LiveMatch(1, 1, radiantTeamBoard = TeamScoreBoard(Team(1), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(2), Nil, Nil, Nil), 1, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      val passed = liveMatchTask.filterOutLessPlayers(liveMatch)
      assert(!passed, "should not passed as number of players less then 5")
    }

    "filterOutLeagues - filter out leagues that are in ban list" in {
      val liveMatch = LiveMatch(1, 1, radiantTeamBoard = TeamScoreBoard(Team(1), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(2), Nil, Nil, Nil), leagueId = 4177, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      val passed = liveMatchTask.filterOutLeagues(liveMatch)
      assert(!passed, "league with this id should be filtered out")

      assert(liveMatchTask.filterOutLeagues(liveMatch.copy(leagueId = 1)), "live match should pass condition as league id is not at the ban list")
    }

    "filterOutLiveMatches - composition of filtering by  number of players and leagueId" in {
      val liveMatch = LiveMatch(1, 1, radiantTeamBoard = TeamScoreBoard(Team(1), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(2), Nil, Nil, Nil), leagueId = 4177, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      val passed = liveMatchTask.filterOutLiveMatches(Some(liveMatch))
      assert(!passed, "Should be filtered out as non plaers condition non league condition is met")
    }

    "findFinishedMatches" in {
      val liveMatch = LiveMatch(19, 1, radiantTeamBoard = TeamScoreBoard(Team(1), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(2), Nil, Nil, Nil), leagueId = 4177, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      val secondLiveMatch = liveMatch.copy(matchId = 20)
      GameDataHolder.setLiveMatchId(liveMatch)
      GameDataHolder.setLiveMatchId(secondLiveMatch)
      val matches = liveMatchTask.findFinishedMatches(List(liveMatch))
      assert(matches.isEmpty, "should be empty as we mark match as finished with second try")
      val finishedMatches = liveMatchTask.findFinishedMatches(List(liveMatch))
      assert(20 == finishedMatches.head, "secondLiveMatch should be defined as finished")
    }

    "processCurrentLiveGame" in {
      val liveMatch = LiveMatch(5, -1, radiantTeamBoard = TeamScoreBoard(Team(45), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(34), Nil, Nil, Nil), leagueId = 4177, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      whenReady(liveMatchTask.processCurrentLiveGame(liveMatch)) {
        m =>
          assert(m.scheduledGameId > 0, " new scheduled game should be created")
          whenReady(Future {
            liveMatchTask.processCurrentLiveGame(liveMatch)
            liveMatchTask.processCurrentLiveGame(liveMatch.copy(matchId = 6))
          }) {
            res =>
              whenReady(TestLookup.seriesRepository.findSeriesByGameId(m.scheduledGameId)) {
                seq =>
                  assert(2 == seq.size, "2 live mathces where inserted with same configuration, so scheduled game should be created only one")
                  val matchesId = seq.map(_.matchId).sorted
                  assert(List(5, 6) == matchesId)
              }
          }
      }
    }

    "processFinishedMatches" in {
      val liveMatch = LiveMatch(matchId = 10, 1, radiantTeamBoard = TeamScoreBoard(Team(1), players = List(Player(1, "", 1, Nil, 1, 1, 1, 1, 1)), Nil, Nil),
        direTeamBoard = TeamScoreBoard(Team(3), Nil, Nil, Nil), leagueId = 417, 0, 0, 0, 0, SeriesType.BO1, 0, 0)
      GameDataHolder.setLiveMatchId(liveMatch)

      whenReady(TestLookup.gameService.findGameByLiveMatch(liveMatch)) {
        game =>
          whenReady(Future {
            TestLookup.matchService.insertNewMatch(liveMatch, game.id, 1)
          }) {
            res =>
              liveMatchTask.processFinishedMatches(10)
              assert(!GameDataHolder.isLiveMatchExists(10), "Match should be removed from live as it finished")
              whenReady(TestLookup.seriesRepository.findSeriesByGameId(game.id)) {
                seq =>
                  val series = seq.head
                  assert(series.finished, "finished field should be updated with true as match was finished")
              }
          }
      }
    }
  }
}
