package com.eb.schedule.dao

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.slick.MatchSeries.MatchSeriesTable
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import org.slf4j.LoggerFactory
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 26.03.2016.
  */
trait SeriesRepository {

  def findSeriesByGameId(id: Int): Future[Seq[MatchSeries]]

  def exists(id: Int, matchId:Long): Future[Boolean]

  def insert(matchSeries: MatchSeries): Future[Int]

  def update(series: MatchSeries): Future[Int]

  def update(matchId: Long, finished: Boolean): Future[Int]

  def getUnfinishedSeries: Future[Seq[(ScheduledGame, MatchSeries)]]

  def getSeriesWithoutWinner: Future[Seq[MatchSeries]]

  def getRunningSeries(): Future[Seq[MatchSeries]]

  def updateMatchWithRadiantWin(matchId: Long, radiantWin: Some[Boolean]): Future[Int]
}

class SeriesRepositoryImpl (implicit db: JdbcBackend#DatabaseDef) extends SeriesRepository {
  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val series = MatchSeries.table

  def filterQuery(id: Int): Query[MatchSeriesTable, MatchSeries, Seq] = series.filter(_.scheduledGameId === id)

  def findSeriesByGameId(id: Int): Future[Seq[MatchSeries]] =
    db.run(filterQuery(id).result)

  def exists(id: Int, matchId:Long): Future[Boolean] =
    db.run(series.filter(series => series.scheduledGameId === id && series.matchId === matchId).exists.result)

  def insert(matchSeries: MatchSeries): Future[Int] = {
    val future: Future[Int] = db.run(series += matchSeries)
    future.onFailure {
      case e => log.error("Couldn't insert Series: " + matchSeries, e)
    }
    future
  }

  def update(seriesGame: MatchSeries): Future[Int] = {
    db.run(
      series.filter(game => game.scheduledGameId === seriesGame.scheduledGameId && game.matchId === seriesGame.matchId).update(seriesGame)
    )
  }

  def update(matchId: Long, finished: Boolean): Future[Int] ={
    db.run(
      series.filter(game => game.matchId === matchId).map(_.finished).update(finished)
    )
  }


  def getUnfinishedSeries(): Future[Seq[(ScheduledGame, MatchSeries)]] = {
    db.run(
      (for {
        (matchSeries, g) <- series join ScheduledGame.table on (_.scheduledGameId === _.id)
        if g.status === MatchStatus.LIVE.status
      } yield (g, matchSeries)).result
    )
  }

  def getSeriesWithoutWinner(): Future[Seq[MatchSeries]] = {
    db.run(
      series.filter(game => game.radiantWin.isEmpty && game.finished).result
    )
  }

  def getRunningSeries(): Future[Seq[MatchSeries]] = {
    db.run(
      series.filter(game => !game.finished).result
    )
  }

  def updateMatchWithRadiantWin(matchId: Long, radiantWin: Some[Boolean]): Future[Int] ={
    db.run(
      series.filter(game => game.matchId === matchId).map(_.radiantWin).update(radiantWin)
    )
  }
}
