package com.eb.pulse.crawler.task

import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.CrawlerUrls
import com.eb.pulse.crawler.service.{GameService, MatchService}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class FindLongRunningGameTask(gameService: GameService, matchService: MatchService, httpUtils: HttpUtils) extends Runnable{

  private val log = LoggerFactory.getLogger(this.getClass)
  private val TWO_HOURS: Long = TimeUnit.HOURS.toMillis(2)

  override def run(): Unit = {
    try {
      val unfinishedSeries = matchService.getNotFinishedGamesWithMatches()
      val lastMatchByGame = unfinishedSeries.map(series => series.map(tuple => (tuple._1, tuple._2.maxBy(_.startDate.getTime))))
      val longRunningGames = lastMatchByGame.map(matches => matches.filter(tuple => System.currentTimeMillis() - tuple._2.startDate.getTime > TWO_HOURS))
      longRunningGames.onComplete {
        case Success(res) => for ((game, lastMatch) <- res) {
          if (lastMatch.finished) {
            gameService.finishTheGame(game.id)
            log.debug("Finishing long running game: {}", game)
          } else {
            if (isFinished(lastMatch.matchId)) {
              matchService.finishMatch(lastMatch.matchId)
              log.debug("Finishing long running match: {}", lastMatch)
            } else {
              log.error("Couldn't finish long running match: " + lastMatch)
            }
          }
        }
        case Failure(ex) => log.error("Issue on finding long running matches", ex)
      }
    } catch {
      case e: Throwable => log.error("Exception while finding long running games", e)
    }
  }

  def isFinished(matchId: Long): Boolean = {
    val response: JsonObject = httpUtils.getResponseAsJson(CrawlerUrls.GET_MATCH_DETAILS + matchId)
    val result: JsonObject = response.getAsJsonObject("result")
    result != null && !result.has("error")
  }
}
