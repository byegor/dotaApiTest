package com.eb.schedule.task

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.live.{GameContainer, LiveGameHelper}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.{ScheduledGameService, UpdateTaskService}
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
class LiveGameTask @Inject()(val liveGameHelper: LiveGameHelper, val netWorthService: NetWorthService, val gameService: ScheduledGameService, val seriesService: SeriesService, val taskService: UpdateTaskService, val httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"

  val finished: mutable.HashSet[Long] = new mutable.HashSet[Long]()

  def run(): Unit = {
    try {
      val liveLeagueGames: List[JsonObject] = getLiveLeagueGames()

      val currentGames: List[Option[CurrentGameDTO]] = liveLeagueGames.map(liveGameHelper.transformToDTO)
      currentGames.filter(filterOutGames).foreach(current => processCurrentLiveGame(current))

      val finishedMatches: Seq[Long] = findFinishedMatches(currentGames)
      finishedMatches.foreach(processFinishedMatches)
    } catch {
      case e: Throwable => log.error("Error during game process", e)
    }
  }

  def filterOutGames(gameOpt: Option[CurrentGameDTO]): Boolean = {
    if (gameOpt.isDefined) {
      val game = gameOpt.get
      //todo move to db or file
      game.basicInfo.league.leagueName != "CDEC Master"
    } else {
      false
    }
  }

  def processCurrentLiveGame(currentOpt: Option[CurrentGameDTO]): Unit = {
    if (currentOpt.isDefined) {
      val current: CurrentGameDTO = currentOpt.get
      if (!GameContainer.exists(current.matchId)) {
        log.debug("found new live game with matchId: " + current)
        val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(current)
        if (scheduledGame.isEmpty) {
          val startDate: Timestamp = new Timestamp(System.currentTimeMillis() - current.basicInfo.duration.toLong)
          val scheduledGameDTO: ScheduledGameDTO = new ScheduledGameDTO(-1, current.radiantTeam, current.direTeam, current.basicInfo.league, current.basicInfo.seriesType, startDate, MatchStatus.LIVE)
          val gameId = gameService.insertAndGet(scheduledGameDTO)
          current.scheduledGameId = gameId
          seriesService.insertOrUpdate(new SeriesDTO(gameId, current.matchId, (current.basicInfo.radiantWin + current.basicInfo.direWin + 1).toByte, None, false, scheduledGameDTO.radiantTeam.id))
          log.debug("creating new scheduled game: " + scheduledGameDTO)
        } else {
          val gameDTO: ScheduledGameDTO = scheduledGame.get
          current.scheduledGameId = gameDTO.id
          gameDTO.matchStatus = MatchStatus.LIVE
          gameService.update(gameDTO).onSuccess {
            case i => seriesService.insertOrUpdate(new SeriesDTO(gameDTO.id, current.matchId, (current.basicInfo.radiantWin + current.basicInfo.direWin + 1).toByte, None, false, gameDTO.radiantTeam.id))
          }
          log.debug("found game: " + current + " for series: " + gameDTO.id)
        }
        addLiveGameContainer(current)
      } else {
        GameContainer.updateJustCurrentGame(current)
      }

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

  def addLiveGameContainer(currentGameDTO: CurrentGameDTO) = {
    GameContainer.addGameAndMapping(currentGameDTO)
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

    clearLiveGameContainer(liveGame)
    storeMatchSeries(matchId)
    finished.remove(matchId)
    log.debug("finished game: " + matchId)
  }

  def storeMatchSeries(matchId: Long) = {
    seriesService.updateFinishedState(matchId, true)
  }

  def clearLiveGameContainer(liveGame: CurrentGameDTO) = {
    GameContainer.removeLiveGame(liveGame.matchId, liveGame.scheduledGameId)
  }
}
