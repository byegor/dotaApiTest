package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.service.{GameService, MatchService}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Get all matches for the game
  * check that all matches is finished
  * count the score to define is game already finished
  * update status for the game
  *
  * Created by Egor on 10.05.2016.
  */
class FindFinishedGamesTask(gameService: GameService, matchService: MatchService) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  override def run(): Unit = {
    try {
      val unfinishedMathcesByGame: Future[Map[ScheduledGame, Seq[MatchSeries]]] = matchService.getNotFinishedGamesWithMatches()
      val shouldBeProcessed: Future[Map[ScheduledGame, Seq[MatchSeries]]] = unfinishedMathcesByGame.map(um => um.filterNot(tuple => tuple._2.exists(_.radiantWin.isEmpty)))

      val gameScoreTuple = shouldBeProcessed.map(g => g.map(tuple => (tuple._1, SeriesType.fromCode(tuple._1.seriesType).gamesCount, tuple._2.count(_.radiantWin.get), tuple._2.count(!_.radiantWin.get))))
      val finishedGames = gameScoreTuple.map(gs => gs.filter(tuple => (tuple._3 * 2 > tuple._2) || (tuple._4 * 2 > tuple._2)))
      finishedGames.onSuccess {
        case res => for (gameTuple <- res) {
          gameService.finishTheGame(gameTuple._1.id)
          log.debug("Set series as finished for: " + gameTuple)
        }
      }
    } catch {
      case e: Throwable => log.error("SeriesCrawler: ", e)
    }
  }
}
