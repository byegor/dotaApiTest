package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.TestLookup
import com.eb.pulse.crawler.model.{LiveMatch, Player, TeamScoreBoard}
import com.eb.schedule.model.{BasicWordSuiteTest, SeriesType}
import com.eb.schedule.model.slick.Team

/**
  * Created by Iegor.Bondarenko on 30.04.2017.
  */
class LiveMatchTaskTest extends BasicWordSuiteTest {

  val liveMatchTask = new LiveMatchTask(TestLookup.gameService, TestLookup.matchService, TestLookup.httpUtils, TestLookup.netWorthService)

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

    }

    "processCurrentLiveGame" in {

    }

    "getLiveMatches" in {

    }


    "processFinishedMatches" in {

    }




  }
}
