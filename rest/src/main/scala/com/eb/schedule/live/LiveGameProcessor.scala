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
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 23.03.2016.
  */
//todo couldn't find scoreboard on start of game - decrease logs
class LiveGameProcessor @Inject()(val liveGameHelper: LiveGameHelper, val netWorthService: NetWorthService, val gameService: ScheduledGameService, val seriesService: SeriesService, val httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  val finished: mutable.HashSet[Long] = new mutable.HashSet[Long]()

  def run(): Unit = {
    try {
      val liveLeagueGames: List[JSONObject] = getLiveLeagueGames()

      val currentGames: List[Option[CurrentGameDTO]] = liveLeagueGames.map(liveGameHelper.transformToDTO)
      currentGames.foreach(current => processCurrentLiveGame(current))

      val finishedMatches: Seq[Long] = findFinishedMatches(currentGames)
      finishedMatches.foreach(processFinishedMatches)
    } catch {
      case e : Throwable => log.error("error", e)
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
          val scheduledGameDTO: ScheduledGameDTO = new ScheduledGameDTO(-1, current.radiantTeam, current.direTeam, current.basicInfo.league, current.basicInfo.seriesType, startDate, MatchStatus.LIVE)
          gameService.insert(scheduledGameDTO).onSuccess {
            case i => updateLiveGameContainer(current)
          }
          log.debug("creating new scheduled game: " + scheduledGameDTO)
        } else {
          val gameDTO: ScheduledGameDTO = scheduledGame.get
          gameDTO.matchStatus = MatchStatus.LIVE
          gameService.update(gameDTO).onSuccess {
            case i => updateLiveGameContainer(current)
          }
          log.debug("update status for game:" + gameDTO.id)
        }
      }
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
    val diff: Seq[Long] = id.toSeq.diff(stillRunning)
    diff.filterNot(id => finished.add(id))
  }

  def processFinishedMatches(matchId: Long): Any = {
    val lgOpt: Option[CurrentGameDTO] = LiveGameContainer.getLiveGame(matchId)
    val liveGame: CurrentGameDTO = lgOpt.get
    val scheduledGame: Option[ScheduledGameDTO] = gameService.getScheduledGames(liveGame, MatchStatus.LIVE)

    val isLastGame: Boolean = isLast(liveGame)
    if (isLastGame) {
      updateGameStatus(scheduledGame.get.id)
    }

    clearLiveGameContainer(liveGame)
    storeMatchSeries(liveGame, scheduledGame.get.id)
    log.debug("finished game: " + matchId)
  }

  def isLast(liveGame: CurrentGameDTO): Boolean = {
    val radiantWin: Byte = liveGame.basicInfo.radiantWin
    val direWin = liveGame.basicInfo.direWin
    val seriesType = liveGame.basicInfo.seriesType
    ((radiantWin + direWin + 1) == seriesType.gamesCount) || (radiantWin * 2 > seriesType.gamesCount) || (direWin * 2 > seriesType.gamesCount)
  }

  def updateGameStatus(gameId: Int) = {
    gameService.updateStatus(gameId, MatchStatus.FINISHED)
  }

  def storeMatchSeries(liveGame: CurrentGameDTO, gameId: Int) = {
    seriesService.insert(new SeriesDTO(gameId, liveGame.matchId, (liveGame.basicInfo.radiantWin + liveGame.basicInfo.direWin + 1).toByte))
  }

  def clearLiveGameContainer(liveGame: CurrentGameDTO) = {
    LiveGameContainer.removeLiveGame(liveGame.matchId)
  }
}
