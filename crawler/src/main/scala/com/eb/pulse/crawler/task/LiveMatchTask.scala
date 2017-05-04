package com.eb.pulse.crawler.task

import java.util

import com.eb.pulse.crawler.cache.CacheHelper
import com.eb.pulse.crawler.data.GameDataHolder
import com.eb.pulse.crawler.model.LiveMatch
import com.eb.pulse.crawler.parser.LiveMatchParser
import com.eb.pulse.crawler.service.{GameService, MatchService, NetworthService, TeamService}
import com.eb.schedule.crawler.CrawlerUrls
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by Egor on 20.04.2017.
  */
class LiveMatchTask(gameService: GameService, matchService: MatchService, httpUtils: HttpUtils, networthService: NetworthService, cacheHelper: CacheHelper, teamService: TeamService) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val liveMatchParser = new LiveMatchParser(networthService, cacheHelper, teamService)

  private val leaguesIdToSkip = getLeagueIdToSkip

  protected val finishedSet: mutable.HashSet[Long] = new mutable.HashSet[Long]()


  override def run(): Unit = {
    val liveMatchesJson: List[JsonObject] = getLiveMatches()
    val parsedLiveMatches: List[LiveMatch] = liveMatchesJson.map(liveMatchParser.parse).filter(filterOutLiveMatches).map(_.get)
    val liveMatches = Future.sequence(parsedLiveMatches.map(processCurrentLiveGame))
    liveMatches.onComplete {
      case Success(liveMatchesResult) =>
        val finishedIds = findFinishedMatches(liveMatchesResult)
        finishedIds.foreach(processFinishedMatches)
        sendMatches(liveMatchesResult)
      case Failure(ex) => log.error("Couldn't process live matches", ex)

    }
  }

  def getLeagueIdToSkip: util.List[Integer] = {
    val config = ConfigFactory.load()
    config.getIntList("skip.league")
  }

  def filterOutLiveMatches(liveMatch: Option[LiveMatch]): Boolean = {
    liveMatch match {
      case Some(m) => filterOutLeagues(m) && filterOutLessPlayers(m)
      case None => false
    }
  }


  def filterOutLessPlayers(liveMatch: LiveMatch): Boolean = {
    liveMatch.radiantTeamBoard.players.size == 5 && liveMatch.direTeamBoard.players.size == 5
  }

  def filterOutLeagues(liveMatch: LiveMatch): Boolean = {
    !leaguesIdToSkip.contains(liveMatch.leagueId)
  }

  def getLiveMatches(): List[JsonObject] = {
    val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_LIVE_LEAGUE_MATCHES)
    var filteredMathces: List[JsonObject] = Nil
    val matchesList: JsonArray = response.getAsJsonObject("result").getAsJsonArray("games")
    for (i <- 0 until matchesList.size()) {
      val liveMatch: JsonObject = matchesList.get(i).getAsJsonObject
      val leagueTier: Int = liveMatch.get("league_tier").getAsInt
      if (leagueTier >= 2) {
        filteredMathces ::= liveMatch
      }
    }
    filteredMathces
  }

  def processCurrentLiveGame(liveMatch: LiveMatch): Future[LiveMatch] = {
    val gameFuture = gameService.findGameByLiveMatch(liveMatch)
    gameFuture.map(game => {
      matchService.insertNewMatch(liveMatch, game.id, game.radiant)
      GameDataHolder.setLiveMatchId(liveMatch)
      liveMatch.copy(scheduledGameId = game.id)
    }
    )
  }

  def findFinishedMatches(liveGames: Seq[LiveMatch]): mutable.Set[Long] = {
    val stillRunning: Set[Long] = liveGames.map(_.matchId).toSet
    val alreadyStoredLiveMatches = GameDataHolder.getLiveMatchesId()
    val diff: mutable.Set[Long] = alreadyStoredLiveMatches.diff(stillRunning)
    diff.filterNot(id => finishedSet.add(id))
  }

  def processFinishedMatches(matchId: Long) {
    matchService.finishMatch(matchId)
    GameDataHolder.removeLiveMatch(matchId)
    finishedSet.remove(matchId)
  }

  def sendMatches(liveMatches: Seq[LiveMatch]) = {
    new SendGameDataTask(liveMatches, gameService, matchService, httpUtils, cacheHelper).execute()
  }
}
