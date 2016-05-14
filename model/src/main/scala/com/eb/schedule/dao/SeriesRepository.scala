package com.eb.schedule.dao

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.MatchSeries.MatchSeriesTable
import com.eb.schedule.model.slick.{MatchSeries, ScheduledGame}
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 26.03.2016.
  */
trait SeriesRepository {

  def findSeriesId(id: Int): Future[Seq[MatchSeries]]

  def exists(id: Int): Future[Boolean]

  def insert(matchSeries: MatchSeries): Future[Int]

  def update(series: MatchSeries): Future[Int]

  def getUnfinishedSeries(): Future[Seq[(ScheduledGame, MatchSeries)]]

  def getSeriesWithoutWinner(): Future[Seq[MatchSeries]]
}

class SeriesRepositoryImpl @Inject()(database: DB) extends SeriesRepository {
  val db: JdbcBackend#DatabaseDef = database.db
  private val log = LoggerFactory.getLogger(this.getClass)

  lazy val series = MatchSeries.table

  def filterQuery(id: Int): Query[MatchSeriesTable, MatchSeries, Seq] = series.filter(_.scheduledGameId === id)

  def findSeriesId(id: Int): Future[Seq[MatchSeries]] =
    db.run(filterQuery(id).result)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

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
      series.filter(game => game.radiantWin.isEmpty).result
    )
  }
}
