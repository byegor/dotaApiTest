package com.eb.schedule.crawler

import java.sql.Timestamp

import com.eb.schedule.dto._
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
                                  liveGameService: LiveGameService,
                                  pickService: PickService,
                                  scheduledGameService: ScheduledGameService
                                ) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run(): Unit = {
    try {
      val liveGamesJson: List[JSONObject] = getLiveGamesJson
      val parsedGamesInfo: List[LiveGameDTO] = liveGamesJson.map(json => extractGameInfo(json))
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

  def extractGameInfo(game: JSONObject): LiveGameDTO = {
    val matchId: Long = game.getLong("match_id")
    val seriesType: Byte = game.getInt("series_type").toByte
    val radiantSeriesWins: Byte = game.getInt("radiant_series_wins").toByte
    val direSeriesWins: Byte = game.getInt("dire_series_wins").toByte

    val radiantTeam: TeamDTO = extractTeam(game.getJSONObject("radiant_team"))
    val direTeam: TeamDTO = extractTeam(game.getJSONObject("dire_team"))
    val league: LeagueDTO = new LeagueDTO(game.getInt("league_id"))

    val scoreBoard: JSONObject = game.getJSONObject("scoreboard")
    val duration: Double = scoreBoard.getDouble("duration")
    val radiantScoreBoard: JSONObject = scoreBoard.getJSONObject("radiant")
    val direScoreBoard: JSONObject = scoreBoard.getJSONObject("dire")

    radiantTeam.score = radiantScoreBoard.getInt("score")
    direTeam.score = direScoreBoard.getInt("score")

    fillTeamDTOWithPicks(radiantTeam, radiantScoreBoard)
    fillTeamDTOWithPicks(direTeam, direScoreBoard)

    val liveGame: LiveGameDTO = new LiveGameDTO(matchId, radiantTeam, direTeam, league, seriesType, new Timestamp(System.currentTimeMillis()), radiantSeriesWins, (radiantSeriesWins + direSeriesWins + 1).toByte)
    liveGame.duration = duration
    liveGame
  }

  private def extractTeam(json: JSONObject): TeamDTO = {
    val teamId: Int = json.getInt("team_id")
    val name = json.getString("team_name")
    new TeamDTO(teamId, name)
  }

  def fillTeamDTOWithPicks(teamDTO: TeamDTO, json: JSONObject): Unit = {
    teamDTO.picks = getTeamPickBan(json.getJSONArray("picks"))
    teamDTO.bans = getTeamPickBan(json.getJSONArray("bans"))
  }

  def getTeamPickBan(picks: JSONArray): List[PickDTO] = {
    var picksResult: List[PickDTO] = Nil
    for (i <- 0 until picks.length()) {
      val pick: JSONObject = picks.getJSONObject(i)
      val hero: Int = pick.getInt("hero_id")
      picksResult ::= new PickDTO(hero)
    }
    picksResult
  }


  def saveGameInfo(liveGameDTO: LiveGameDTO): Unit = {
    val isGameExists: Boolean = Await.result(liveGameService.exists(liveGameDTO.matchId), Duration.Inf)
    if (!isGameExists) {
      val insert: Future[Int] = liveGameService.insert(liveGameDTO)
      val game: Option[ScheduledGameDTO] = scheduledGameService.getScheduledGames(liveGameDTO)
      if (game.isDefined) {
        scheduledGameService.updateStatus(game.get.id, MatchStatus.LIVE.status)
      } else {
        insert.andThen{
          case s => scheduledGameService.insert(new ScheduledGameDTO(-1, Some(liveGameDTO.matchId), liveGameDTO.radiant, liveGameDTO.dire, liveGameDTO.leagueDTO, liveGameDTO.startDate, MatchStatus.LIVE.status, liveGameDTO.radiant.score.toByte, liveGameDTO.dire.score.toByte))
        }
      }
    } else {
      scheduledGameService.updateScore(liveGameDTO.matchId, liveGameDTO.radiant.score.toByte, liveGameDTO.dire.score.toByte)
      if (liveGameDTO.duration < 200) {
        pickService.insertIfNotExists(liveGameDTO.matchId, liveGameDTO.radiant, true)
        pickService.insertIfNotExists(liveGameDTO.matchId, liveGameDTO.dire, false)
      }
    }
  }

}
