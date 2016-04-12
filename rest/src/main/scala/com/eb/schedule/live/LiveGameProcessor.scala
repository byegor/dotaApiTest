package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.model.MatchStatus
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

class LiveGameProcessor @Inject()(val liveGameHelper: LiveGameHelper, val netWorthService: NetWorthService, val gameService: ScheduledGameService, val seriesService: SeriesService, val httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  def process(): Unit = {
    val liveLeagueGames: List[JSONObject] = getLiveLeagueGames()

    val currentGames: List[CurrentGameDTO] = liveLeagueGames.map(liveGameHelper.transformToDTO)
    currentGames.foreach(current => processCurrentLiveGame(current))

    val finishedMatches: Seq[Long] = findFinishedMatches(currentGames)
    finishedMatches.foreach(processFinishedMatches)
  }

  def processFinishedMatches(matchId: Long): Any = {
    val future: Future[Option[ScheduledGameDTO]] = gameService.findByMatchId(matchId)
    val lgOpt: Option[CurrentGameDTO] = LiveGameContainer.getLiveGame(matchId)
    val liveGame: CurrentGameDTO = lgOpt.get
    val scheduledGame: Option[ScheduledGameDTO] = Await.result(future, Duration.Inf)
    if (scheduledGame.isDefined) {
      storeMatchSeries(liveGame, scheduledGame.get.id)
      clearLiveGameContainer(lgOpt.get)
    } else {
      log.error("WTF scheduled game should be there: " + matchId)
    }
  }

  def processCurrentLiveGame(current: CurrentGameDTO): Unit = {
    if (!LiveGameContainer.exists(current.matchId)) {
      log.debug("found new live game with matchId: " + current.matchId)
      val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(current)
      if (scheduledGame.isEmpty) {
        val startDate: Timestamp = new Timestamp(System.currentTimeMillis() - current.basicInfo.duration.toLong)
        val scheduledGameDTO: ScheduledGameDTO = new ScheduledGameDTO(-1, Some(current.matchId), current.radiantTeam, current.direTeam, current.basicInfo.league, startDate, MatchStatus.LIVE)
        gameService.insert(scheduledGameDTO)
        log.debug("creating new scheduled game: " + scheduledGameDTO)
      } else {
        val gameDTO: ScheduledGameDTO = scheduledGame.get
        if (gameDTO.matchStatus == MatchStatus.SCHEDULED) {
          gameDTO.matchStatus = MatchStatus.LIVE
          gameDTO.matchId = Some(current.matchId)
          gameService.update(gameDTO)
          log.debug("update status for game:" + gameDTO.matchId)
        }
      }
    }
    updateLiveGameContainer(current)
    insertNetWorth(current.netWorth)
  }

  def getLiveLeagueGames(): List[JSONObject] = {
    val response: JSONObject = httpUtils.getResponseAsJson(GET_LIVE_LEAGUE_MATCHES)
    var gamesJson: List[JSONObject] = Nil
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

  def insertNetWorth(netWorth: NetWorthDTO) = {
    netWorthService.insertOrUpdate(netWorth)
  }

  def updateLiveGameContainer(currentGameDTO: CurrentGameDTO) = {
    LiveGameContainer.updateLiveGame(currentGameDTO)
  }

  def findFinishedMatches(liveGames: Seq[CurrentGameDTO]): Seq[Long] = {
    val stillRunning: Seq[Long] = liveGames.map(_.matchId)
    val id: Iterable[Long] = LiveGameContainer.getLiveMatchesId()
    id.toSeq.diff(stillRunning)
  }


  def updateGameStatus(gameId: Int) = {
    gameService.updateStatus(gameId, MatchStatus.FINISHED.status)
  }

  def storeMatchSeries(liveGame: CurrentGameDTO, gameId: Int) = {
    updateGameStatus(gameId)
    seriesService.insert(new SeriesDTO(gameId, liveGame.matchId, (liveGame.basicInfo.radiantWin + liveGame.basicInfo.direWin + 1).toByte))
  }

  def clearLiveGameContainer(liveGame: CurrentGameDTO) = {
    LiveGameContainer.removeLiveGame(liveGame.matchId)
  }
}
