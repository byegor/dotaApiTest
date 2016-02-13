package com.eb.schedule.crawler

import com.eb.schedule.utils.{DBUtils, HttpUtils}
import egor.dota.model.entity._
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 10.02.2016.
  */
//todo what if series
class LiveMatchCrawler extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)

  def run(): Unit ={
    try{
      val liveGamesJson: List[JSONObject] = getLiveMatchesJson
      val parsedMatch: List[Match] = liveGamesJson.map(json => parseMatch(json))
      parsedMatch.foreach(saveOrUpdateLiveMatch)
    }catch {
      case e:Exception => log.error("exception when running live match crawler ", e)
    }
  }

  def getLiveMatchesJson = {
    var gamesJson: List[JSONObject] = Nil
    val response: JSONObject = HttpUtils.getResponseAsJson(CrawlerUrls.GET_LIVE_LEAGUE_MATCHES)
    val gamesList: JSONArray = response.getJSONObject("result").getJSONArray("games")
    for (i <- 0 until gamesList.length()) {
      val game: JSONObject = gamesList.getJSONObject(i)
      val leagueTier: Int = game.getInt("league_tier")
      if (leagueTier == 3) {
        gamesJson ::= game
      }
    }
    gamesJson
  }

  def parseMatch(game: JSONObject): Match = {
    val radiantTeam: JSONObject = game.getJSONObject("radiant_team")
    val direTeam: JSONObject = game.getJSONObject("dire_team")
    val radiantTeamId: Int = radiantTeam.getInt("team_id")
    val direTeamId: Int = direTeam.getInt("team_id")
    val matchId: Long = game.getLong("match_id")
    val leagueId: Int = game.getInt("league_id")
    val seriesType: Byte = game.getInt("series_type").toByte
    val radiantSeriesWins: Byte = game.getInt("radiant_series_wins").toByte
    val direSeriesWins: Byte = game.getInt("dire_series_wins").toByte
    new Match(matchId, radiantTeamId, direTeamId, leagueId, seriesType, radiantSeriesWins, (radiantSeriesWins + direSeriesWins + 1).toByte)
  }

  def saveOrUpdateLiveMatch(matchDetails: Match): Unit = {
    val gameOpt: Option[Game] = DBUtils.getNotFinishedMatchByCriteria(matchDetails.matchId, matchDetails.radiantId, matchDetails.direId)
    if (gameOpt.isDefined) {
      val game: Game = gameOpt.get
      if (MatchStatus.SCHEDULED == game.status) {
        DBUtils.updateGameStatus(game)
      }
    }else{
      if(!DBUtils.getTeamById(matchDetails.radiantId).isDefined){
        DBUtils.updateOrCreateTeamInfo(new TeamInfo(matchDetails.radiantId, "", ""))
      }
      if(!DBUtils.getTeamById(matchDetails.direId).isDefined){
        DBUtils.updateOrCreateTeamInfo(new TeamInfo(matchDetails.direId, "", ""))
      }
      if(!DBUtils.getLeagueById(matchDetails.leagueid).isDefined){
        DBUtils.saveLeague(new League(matchDetails.leagueid, "", "", ""))
      }
      DBUtils.saveMatchDetails(matchDetails)
      DBUtils.saveLiveGame(matchDetails)
    }
  }

}
