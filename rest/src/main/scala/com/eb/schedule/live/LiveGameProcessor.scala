package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.model.{MatchStatus, SeriesType}
import com.eb.schedule.model.services.ScheduledGameService
import com.eb.schedule.services.{NetWorthService, SeriesService}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 23.03.2016.
  */
//todo league crawler don't update task status
//todo insertNew game failed in mysql but to container success
//todo on restart containers is empty but the game was finished - couldn't update status
//todo couldn't find scoreboard on start of game - decrease logs

//todo remember that sometime steam return empty list of games
class LiveGameProcessor @Inject()(val liveGameHelper: LiveGameHelper, val netWorthService: NetWorthService, val gameService: ScheduledGameService, val seriesService: SeriesService, val httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  def run(): Unit = {
    try {
      val liveLeagueGames: List[JSONObject] = getLiveLeagueGames()

      val currentGames: List[Option[CurrentGameDTO]] = liveLeagueGames.map(liveGameHelper.transformToDTO)
      currentGames.foreach(current => processCurrentLiveGame(current))

      val finishedMatches: Seq[Long] = findFinishedMatches(currentGames)
      finishedMatches.foreach(processFinishedMatches)
    } catch {
      case e => log.error("error", e)
    }
  }

  def processCurrentLiveGame(currentOpt: Option[CurrentGameDTO]): Unit = {
    if (currentOpt.isDefined) {
      val current: CurrentGameDTO = currentOpt.get
      if (!LiveGameContainer.exists(current.matchId)) {
        log.debug("found new live game with matchId: " + current.matchId)
        val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(current, MatchStatus.SCHEDULED)
        if (scheduledGame.isEmpty) {
          val startDate: Timestamp = new Timestamp(System.currentTimeMillis() - current.basicInfo.duration.toLong)
          val scheduledGameDTO: ScheduledGameDTO = new ScheduledGameDTO(-1, current.radiantTeam, current.direTeam, current.basicInfo.league, startDate, MatchStatus.LIVE)
          gameService.insert(scheduledGameDTO)
          log.debug("creating new scheduled game: " + scheduledGameDTO)
        } else {
          val gameDTO: ScheduledGameDTO = scheduledGame.get
          gameDTO.matchStatus = MatchStatus.LIVE
          gameService.update(gameDTO)
          log.debug("update status for game:" + gameDTO.id)
        }
      }
      updateLiveGameContainer(current)
      if (current.basicInfo.duration > 0) {
        insertNetWorth(current.netWorth)
      }
    }
  }

  def getLiveLeagueGames(): List[JSONObject] = {
    val response: JSONObject = httpUtils.getResponseAsJson(GET_LIVE_LEAGUE_MATCHES)
    var gamesJson: List[JSONObject] = Nil
    val gamesList: JSONArray = response.getJSONObject("result").getJSONArray("games")
    for (i <- 0 until gamesList.length()) {
      val game: JSONObject = gamesList.getJSONObject(i)
      val leagueTier: Int = game.getInt("league_tier")
      if (leagueTier >= 2) {
        gamesJson ::= game
      }
    }
    gamesJson
  }

  def insertNetWorth(netWorth: NetWorthDTO) = {
    netWorthService.insertOrUpdate(netWorth)
  }

  def updateLiveGameContainer(currentGameDTO: CurrentGameDTO) = {
    LiveGameContainer.updateLiveGame(currentGameDTO)
  }

  def findFinishedMatches(liveGames: Seq[Option[CurrentGameDTO]]): Seq[Long] = {
    val stillRunning: Seq[Long] = liveGames.filter(_.isDefined).map(_.get.matchId)
    val id: Iterable[Long] = LiveGameContainer.getLiveMatchesId()
    id.toSeq.diff(stillRunning)
  }

  def processFinishedMatches(matchId: Long): Any = {
    val lgOpt: Option[CurrentGameDTO] = LiveGameContainer.getLiveGame(matchId)
    val liveGame: CurrentGameDTO = lgOpt.get
    val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(liveGame, MatchStatus.LIVE)

    val isLastGame: Boolean = isLast(liveGame.basicInfo.radiantWin, liveGame.basicInfo.direWin, liveGame.basicInfo.seriesType)
    if (isLastGame) {
      updateGameStatus(scheduledGame.get.id)
    }

    clearLiveGameContainer(liveGame)
    storeMatchSeries(liveGame, scheduledGame.get.id)
  }

  def isLast(radiantWin: Byte, direWin: Byte, seriesType: SeriesType): Boolean = {
    ((radiantWin + direWin + 1) == seriesType.gamesCount) || (radiantWin * 2 > seriesType.gamesCount) || (direWin * 2 > seriesType.gamesCount)
  }

  def updateGameStatus(gameId: Int) = {
    gameService.updateStatus(gameId, MatchStatus.FINISHED.status)
  }

  def storeMatchSeries(liveGame: CurrentGameDTO, gameId: Int) = {
    seriesService.insert(new SeriesDTO(gameId, liveGame.matchId, (liveGame.basicInfo.radiantWin + liveGame.basicInfo.direWin + 1).toByte))
  }

  def clearLiveGameContainer(liveGame: CurrentGameDTO) = {
    LiveGameContainer.removeLiveGame(liveGame.matchId)
  }
}
