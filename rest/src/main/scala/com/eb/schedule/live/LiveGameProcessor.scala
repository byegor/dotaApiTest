package com.eb.schedule.live

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.{ScheduledGameService, UpdateTaskService}
import com.eb.schedule.model.slick.{MatchSeries, UpdateTask}
import com.eb.schedule.services.{NetWorthService, SeriesService}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 23.03.2016.
  */
//todo log to file
//todo bo2
//todo get all from cache
class LiveGameProcessor @Inject()(val liveGameHelper: LiveGameHelper, val netWorthService: NetWorthService, val gameService: ScheduledGameService, val seriesService: SeriesService, val taskService: UpdateTaskService, val httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  val finished: mutable.HashSet[Long] = new mutable.HashSet[Long]()

  def run(): Unit = {
    try {
      val liveLeagueGames: List[JsonObject] = getLiveLeagueGames()

      val currentGames: List[Option[CurrentGameDTO]] = liveLeagueGames.map(liveGameHelper.transformToDTO)
      currentGames.foreach(current => processCurrentLiveGame(current))

      val finishedMatches: Seq[Long] = findFinishedMatches(currentGames)
      finishedMatches.foreach(processFinishedMatches)
    } catch {
      case e: Throwable => log.error("error", e)
    }
  }

  def processCurrentLiveGame(currentOpt: Option[CurrentGameDTO]): Unit = {
    if (currentOpt.isDefined) {
      val current: CurrentGameDTO = currentOpt.get
      if (!GameContainer.exists(current.matchId)) {
        log.debug("found new live game with matchId: " + current.matchId)
        val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(current)
        if (scheduledGame.isEmpty) {
          val startDate: Timestamp = new Timestamp(System.currentTimeMillis() - current.basicInfo.duration.toLong)
          val scheduledGameDTO: ScheduledGameDTO = new ScheduledGameDTO(-1, current.radiantTeam, current.direTeam, current.basicInfo.league, current.basicInfo.seriesType, startDate, MatchStatus.LIVE)
          gameService.insertAndGet(scheduledGameDTO).onSuccess {
            case gameId =>
              seriesService.insertOrUpdate(new SeriesDTO(gameId, current.matchId, (current.basicInfo.radiantWin + current.basicInfo.direWin + 1).toByte, None, false))
          }
          log.debug("creating new scheduled game: " + scheduledGameDTO)
        } else {
          val gameDTO: ScheduledGameDTO = scheduledGame.get
          gameDTO.matchStatus = MatchStatus.LIVE
          gameService.update(gameDTO).onSuccess {
            case i => seriesService.insertOrUpdate(new SeriesDTO(gameDTO.id, current.matchId, (current.basicInfo.radiantWin + current.basicInfo.direWin + 1).toByte, None, false))
          }
          log.debug("found game: " + current.matchId + " for series: " + gameDTO.id)
        }
      }
      updateLiveGameContainer(current)
      if (current.basicInfo.duration > 0) {
        insertNetWorth(current.netWorth)
      }
    }
  }

  def getLiveLeagueGames(): List[JsonObject] = {
    val response: JsonObject = httpUtils.getResponseAsJson(GET_LIVE_LEAGUE_MATCHES)
    var gamesJson: List[JsonObject] = Nil
    val gamesList: JsonArray = response.getAsJsonObject("result").getAsJsonArray("games")
    for (i <- 0 until gamesList.size()) {
      val game: JsonObject = gamesList.get(i).getAsJsonObject()
      val leagueTier: Int = game.get("league_tier").getAsInt
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
    GameContainer.updateLiveGame(currentGameDTO)
  }

  def findFinishedMatches(liveGames: Seq[Option[CurrentGameDTO]]): Seq[Long] = {
    val stillRunning: Seq[Long] = liveGames.filter(_.isDefined).map(_.get.matchId)
    val id: Iterable[Long] = GameContainer.getLiveMatchesId()
    val diff: Seq[Long] = id.toSeq.diff(stillRunning)
    diff.filterNot(id => finished.add(id))
  }

  def processFinishedMatches(matchId: Long) {
    val lgOpt: Option[CurrentGameDTO] = GameContainer.getLiveGame(matchId)
    val liveGame: CurrentGameDTO = lgOpt.get
    val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(liveGame, MatchStatus.LIVE)

    clearLiveGameContainer(liveGame)
    storeMatchSeries(matchId)
    finished.remove(matchId)
    log.debug("finished game: " + matchId + " series id: " + scheduledGame.get.id)
  }

  def storeMatchSeries(matchId: Long) = {
    seriesService.updateFinishedState(matchId, true)
  }

  def clearLiveGameContainer(liveGame: CurrentGameDTO) = {
    GameContainer.removeLiveGame(liveGame.matchId)
  }
}
