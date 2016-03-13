package com.eb.schedule.crawler

import com.eb.schedule.dto.{LiveGameDTO, PickDTO, TeamDTO}
import com.eb.schedule.model.BasicTest
import org.json.{JSONArray, JSONObject}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.BufferedSource

/**
  * Created by Egor on 11.03.2016.
  */
class LiveMatchCrawlerTest extends BasicTest {

  val crawler = new LiveMatchCrawler(teamService, liveGameService, pickService, scheduledGameService)

  test("extract Live game") {
    val liveGameDTO: LiveGameDTO = crawler.extractGameInfo(liveMatchJson)

    val radiantTeam: TeamDTO = liveGameDTO.radiant
    assert(2593210 == radiantTeam.id)
    assert("44HARDCORE ESPORTS" == radiantTeam.name)
    assert("" == radiantTeam.tag)

    val radiantPick: List[PickDTO] = List(new PickDTO(30), new PickDTO(70), new PickDTO(92),  new PickDTO(41), new PickDTO(13)).sortBy(_.heroId)
    val radiantBan: List[PickDTO] = List(new PickDTO(43), new PickDTO(74), new PickDTO(72),  new PickDTO(18), new PickDTO(47)).sortBy(_.heroId)
    assert(radiantTeam.picks.sortBy(_.heroId) == radiantPick)
    assert(radiantTeam.bans.sortBy(_.heroId) == radiantBan)

    assert(3 == radiantTeam.score, "score for radiant was parsed wrong")

    val direTeam: TeamDTO = liveGameDTO.dire
    assert(2312626 == direTeam.id)
    assert("-Do4a" == direTeam.name)
    assert("" == direTeam.tag)

    val direPick: List[PickDTO] = List(new PickDTO(20), new PickDTO(29), new PickDTO(6),  new PickDTO(66), new PickDTO(46)).sortBy(_.heroId)
    val direBan: List[PickDTO] = List(new PickDTO(80), new PickDTO(76), new PickDTO(39),  new PickDTO(67), new PickDTO(22)).sortBy(_.heroId)
    assert(direTeam.picks.sortBy(_.heroId) == direPick)
    assert(direTeam.bans.sortBy(_.heroId) == direBan)

    assert(2 == direTeam.score, "score for dire was parsed wrong")

    assert(4446 == liveGameDTO.leagueDTO.id)
    assert("" == liveGameDTO.leagueDTO.name)

    assert(529.83728 ==liveGameDTO.duration)
  }

  test("store new live game"){
    val liveGameDTO: LiveGameDTO = crawler.extractGameInfo(liveMatchJson)

    crawler.saveGameInfo(liveGameDTO)
    var cnt = 0
    while(!Await.result(liveGameService.exists(2215228651L), Duration.Inf) && cnt < 10){
      cnt = cnt + 1
      Thread.sleep(300)
    }

    assert(Await.result(teamService.exists(liveGameDTO.radiant.id), Duration.Inf), "radiant team not saved")
    assert(Await.result(teamService.exists(liveGameDTO.dire.id), Duration.Inf), "dire team not saved")
    assert(Await.result(leagueService.exists(liveGameDTO.leagueDTO.id), Duration.Inf), "league not saved")
    assert(Await.result(liveGameService.exists(liveGameDTO.matchId), Duration.Inf), "live game not saved")
    assert(Await.result(scheduledGameService.findByMatchId(liveGameDTO.matchId), Duration.Inf) != null, "scheduled game not saved")
    //todo check picks
  }

  //todo test updated picks and score
  //todo with existing scheduled game


  def getLiveMatchResponse: JSONObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
    val lines = try source.mkString finally source.close()
    val liveMatchResult: JSONObject = new JSONObject(lines)
    liveMatchResult
  }

  val liveMatchJson: JSONObject = {
    val response: JSONObject = getLiveMatchResponse
    val gamesList: JSONArray = response.getJSONObject("result").getJSONArray("games")
    val nObject: JSONObject = gamesList.getJSONObject(0)
    nObject
  }
}
