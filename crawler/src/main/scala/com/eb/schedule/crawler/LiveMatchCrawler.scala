package com.eb.schedule.crawler

import java.sql.Timestamp


import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.dao.{LiveGameDao, ScheduledGamesDao, TeamDao, LeagueDao}
import com.eb.schedule.model.slick.{ScheduledGame, Pick, LiveGame}
import com.eb.schedule.utils.HttpUtils
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Future}
import ExecutionContext.Implicits.global

/**
  * Created by Egor on 10.02.2016.
  */
//todo what if series
class LiveMatchCrawler extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)

  def run(): Unit ={
    try{
      val liveGamesJson: List[JSONObject] = getLiveGamesJson
      val parsedLiveGames: List[LiveGame] = liveGamesJson.map(json => parseLiveGame(json))
      //todo
      val parsedScoreBoards: List[(List[Pick], (Int, Int))] = liveGamesJson.map(extractScoreBoardDetails(_))
      parsedLiveGames.foreach(saveLiveGame)
    }catch {
      case e:Exception => log.error("exception when running live match crawler ", e)
    }
  }

  def getLiveGamesJson = {
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

  def parseLiveGame(game: JSONObject): LiveGame = {
    val radiantTeam: JSONObject = game.getJSONObject("radiant_team")
    val direTeam: JSONObject = game.getJSONObject("dire_team")
    val radiantTeamId: Int = radiantTeam.getInt("team_id")
    val direTeamId: Int = direTeam.getInt("team_id")
    val matchId: Long = game.getLong("match_id")
    val leagueId: Int = game.getInt("league_id")
    val seriesType: Byte = game.getInt("series_type").toByte
    val radiantSeriesWins: Byte = game.getInt("radiant_series_wins").toByte
    val direSeriesWins: Byte = game.getInt("dire_series_wins").toByte
    new LiveGame(matchId, radiantTeamId, direTeamId, leagueId, seriesType, new Timestamp(System.currentTimeMillis()), radiantSeriesWins, (radiantSeriesWins + direSeriesWins + 1).toByte)
  }

  def extractScoreBoardDetails(game:JSONObject) = {
    val matchId: Long = game.getLong("match_id")
    val scoreBoard: JSONObject = game.getJSONObject("scoreboard")
    val radiantScoreBoard: JSONObject = scoreBoard.getJSONObject("radiant")
    val direScoreBoard: JSONObject = scoreBoard.getJSONObject("dire")
    val radiantScore: Int = radiantScoreBoard.getInt("score")
    val direScore: Int = direScoreBoard.getInt("score")
    val scoreTuple = (radiantScore, direScore)
    val picks: List[Pick] = getPicks(matchId, direScoreBoard, radiantScoreBoard)
    (picks, scoreTuple)
  }

  def getPicks(matchId: Long, direScoreBoard: JSONObject, radiantScoreBoard: JSONObject): List[Pick] ={
    getTeamPickBan(direScoreBoard.getJSONArray("picks"), matchId, isPick = true, isRadiant = false) :::
      getTeamPickBan(direScoreBoard.getJSONArray("bans"), matchId, isPick = false, isRadiant = false) :::
      getTeamPickBan(direScoreBoard.getJSONArray("picks"), matchId, isPick = true, isRadiant = true) :::
      getTeamPickBan(direScoreBoard.getJSONArray("bans"), matchId, isPick = false, isRadiant = false)
  }

  def getTeamPickBan(picks: JSONArray, matchId: Long, isPick:Boolean, isRadiant:Boolean): List[Pick] = {
    var picksResult: List[Pick] = Nil
    for (i <- 0 until picks.length()) {
      val pick: JSONObject = picks.getJSONObject(i)
      val hero: Int = pick.getInt("hero_id")
      picksResult ::= new Pick(matchId, hero, isRadiant, isPick)
    }
    picksResult
  }


  def saveLiveGame(liveGame: LiveGame): Unit = {
    val isNewGame: Boolean = Await.result(LiveGameDao.exists(liveGame.matchId), Duration.fromNanos(5000))
    if(isNewGame){
      TeamDao.insertTeamTask(liveGame.radiant)
      TeamDao.insertTeamTask(liveGame.dire)
      LeagueDao.insertLeagueTask(liveGame.leagueId)
      LiveGameDao.insert(liveGame)
      if( liveGame.game == 0){
        val game: Future[ScheduledGame] = ScheduledGamesDao.getScheduledGames(liveGame)
        //todo what if we don't find a game
        game onFailure {
          case t => println("An error has occured: " + t.getMessage)
        }
        game onSuccess {
          case g =>{
            ScheduledGamesDao.update(new ScheduledGame(g.id, Some(liveGame.matchId), g.radiant, g.dire, g.leagueId, g.startDate, MatchStatus.LIVE.status))
          }
        }

      }
    }
  }

}
