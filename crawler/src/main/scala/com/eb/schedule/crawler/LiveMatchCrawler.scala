package com.eb.schedule.crawler

import java.sql.Timestamp
import java.util.Date

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.dao.{MatchDao, GameDao, TeamDao, LeagueDao}
import com.eb.schedule.model.slick.{Game, MatchDetails}
import com.eb.schedule.utils.{HttpUtils}
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 10.02.2016.
  */
//todo what if series
class LiveMatchCrawler extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)

  def run(): Unit ={
    try{
      val liveGamesJson: List[JSONObject] = getLiveMatchesJson
      val parsedMatch: List[MatchDetails] = liveGamesJson.map(json => parseMatch(json))
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

  def parseMatch(game: JSONObject): MatchDetails = {
    val radiantTeam: JSONObject = game.getJSONObject("radiant_team")
    val direTeam: JSONObject = game.getJSONObject("dire_team")
    val radiantTeamId: Int = radiantTeam.getInt("team_id")
    val direTeamId: Int = direTeam.getInt("team_id")
    val matchId: Long = game.getLong("match_id")
    val leagueId: Int = game.getInt("league_id")
    val seriesType: Byte = game.getInt("series_type").toByte
    val radiantSeriesWins: Byte = game.getInt("radiant_series_wins").toByte
    val direSeriesWins: Byte = game.getInt("dire_series_wins").toByte
    new MatchDetails(matchId, radiantTeamId, direTeamId, leagueId, seriesType, radiantSeriesWins, (radiantSeriesWins + direSeriesWins + 1).toByte)
  }

  //todo
  def saveOrUpdateLiveMatch(matchDetails: MatchDetails): Unit = {
    val games: Future[Seq[Game]] = GameDao.getUnfinishedGames(matchDetails)
    val result: Seq[Game] = Await.result(games, Duration.Inf)
    if (gameOpt.isDefined) {
      val game: Game = gameOpt.get
      if (MatchStatus.SCHEDULED == game.status) {
        DBUtils.updateGameStatus(game)
      }
    }else{
      TeamDao.insertTeamTask(matchDetails.radiant)
      TeamDao.insertTeamTask(matchDetails.dire)
      LeagueDao.insertLeagueTask(matchDetails.leagueId)

      MatchDao.insert(matchDetails)
      GameDao.insert(new Game(-1, matchDetails.radiant, matchDetails.dire, matchDetails.leagueId, Some(matchDetails.matchId), Some(new Timestamp(System.currentTimeMillis())), MatchStatus.LIVE))

    }
  }

}
