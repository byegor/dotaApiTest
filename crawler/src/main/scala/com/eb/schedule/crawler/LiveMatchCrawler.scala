package com.eb.schedule.crawler

import java.sql.Timestamp

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services._
import com.eb.schedule.model.slick.{LiveGame, Pick, ScheduledGame}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 10.02.2016.
  */
class LiveMatchCrawler @Inject()(
                                  teamService: TeamService,
                                  leagueService: LeagueService,
                                  liveGameService: LiveGameService,
                                  pickService: PickService,
                                  scheduledGameService: ScheduledGameService
                                ) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run(): Unit = {
    try {
      val liveGamesJson: List[JSONObject] = getLiveGamesJson
      val parsedGamesInfo: List[(LiveGame, List[Pick], Int, Int)] = liveGamesJson.map(json => extractGameInfo(json))

      parsedGamesInfo.foreach(saveGameInfo)
    } catch {
      case e: Exception => log.error("exception when running live match crawler ", e)
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

  def extractGameInfo(game: JSONObject): (LiveGame, List[Pick], Int, Int) = {
    val radiantTeam: JSONObject = game.getJSONObject("radiant_team")
    val direTeam: JSONObject = game.getJSONObject("dire_team")
    val radiantTeamId: Int = radiantTeam.getInt("team_id")
    val direTeamId: Int = direTeam.getInt("team_id")
    val matchId: Long = game.getLong("match_id")
    val leagueId: Int = game.getInt("league_id")
    val seriesType: Byte = game.getInt("series_type").toByte
    val radiantSeriesWins: Byte = game.getInt("radiant_series_wins").toByte
    val direSeriesWins: Byte = game.getInt("dire_series_wins").toByte
    val scoreBoard: JSONObject = game.getJSONObject("scoreboard")
    val duration: Double = scoreBoard.getDouble("duration")
    val radiantScoreBoard: JSONObject = scoreBoard.getJSONObject("radiant")
    val direScoreBoard: JSONObject = scoreBoard.getJSONObject("dire")
    val radiantScore: Int = radiantScoreBoard.getInt("score")
    val direScore: Int = direScoreBoard.getInt("score")
    val picks: List[Pick] = getPicks(matchId, direScoreBoard, radiantScoreBoard)
    val liveGame: LiveGame = new LiveGame(matchId, radiantTeamId, direTeamId, leagueId, seriesType, new Timestamp(System.currentTimeMillis()), radiantSeriesWins, (radiantSeriesWins + direSeriesWins + 1).toByte)
    (liveGame, picks, radiantScore, direScore)
  }

  def getPicks(matchId: Long, direScoreBoard: JSONObject, radiantScoreBoard: JSONObject): List[Pick] = {
    getTeamPickBan(direScoreBoard.getJSONArray("picks"), matchId, isPick = true, isRadiant = false) :::
      getTeamPickBan(direScoreBoard.getJSONArray("bans"), matchId, isPick = false, isRadiant = false) :::
      getTeamPickBan(radiantScoreBoard.getJSONArray("picks"), matchId, isPick = true, isRadiant = true) :::
      getTeamPickBan(radiantScoreBoard.getJSONArray("bans"), matchId, isPick = false, isRadiant = true)
  }

  def getTeamPickBan(picks: JSONArray, matchId: Long, isPick: Boolean, isRadiant: Boolean): List[Pick] = {
    var picksResult: List[Pick] = Nil
    for (i <- 0 until picks.length()) {
      val pick: JSONObject = picks.getJSONObject(i)
      val hero: Int = pick.getInt("hero_id")
      picksResult ::= new Pick(matchId, hero, isRadiant, isPick)
    }
    picksResult
  }


  def saveGameInfo(gameInfo: (LiveGame, List[Pick], Int, Int)): Unit = {
    val liveGame: LiveGame = gameInfo._1
    val isGameExists: Boolean = Await.result(liveGameService.exists(liveGame.matchId), Duration.Inf)
    if (!isGameExists) {
      teamService.insertTeamTask(liveGame.radiant)
      teamService.insertTeamTask(liveGame.dire)
      leagueService.insertLeagueTask(liveGame.leagueId)
      liveGameService.insert(liveGame)
      val game: Future[Option[ScheduledGame]] = scheduledGameService.getScheduledGames(liveGame)
      game onSuccess {
        case Some(g) => scheduledGameService.updateStatus(g.id, MatchStatus.LIVE.status)
        case None => scheduledGameService.insert(new ScheduledGame(-1, Some(liveGame.matchId), liveGame.radiant, liveGame.dire, liveGame.leagueId, liveGame.startDate, MatchStatus.LIVE.status, gameInfo._3.toByte, gameInfo._4.toByte))
      }
    } else {
      scheduledGameService.updateScore(liveGame.matchId, gameInfo._3.toByte, gameInfo._4.toByte)
    }
    if (gameInfo._2 != Nil) {
      gameInfo._2.foreach(pickService.updateOrCreate)
    }
  }

}
