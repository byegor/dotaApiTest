package com.eb.schedule.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.{Item, MatchSeries}
import com.eb.schedule.model.slick.Item.ItemTable
import com.eb.schedule.model.slick.MatchSeries.MatchSeriesTable
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

/**
  * Created by Egor on 26.03.2016.
  */
trait SeriesRepository {

  def findSeriesId(id: Int): Future[MatchSeries]

  def exists(id: Int): Future[Boolean]

  def insert(matchSeries: MatchSeries)
}

class SeriesRepositoryImpl @Inject()(database: DB) extends SeriesRepository {
  val db: JdbcBackend#DatabaseDef = database.db

  lazy val series = MatchSeries.table

  def filterQuery(id: Int): Query[MatchSeriesTable, MatchSeries, Seq] = series.filter(_.scheduledGameId === id)

  def findSeriesId(id: Int): Future[MatchSeries] =
    db.run(filterQuery(id).result.head)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(matchSeries: MatchSeries): Unit = {
    db.run(series += matchSeries)
  }
}
