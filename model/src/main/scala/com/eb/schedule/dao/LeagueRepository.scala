package com.eb.schedule.model.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.League.LeaguesTable
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */

trait LeagueRepository {

  def findById(id: Int): Future[Option[League]]

  def exists(id: Int): Future[Boolean]

  def insert(league: League): Future[Int]

  def insertLeagueTask(league: League): Future[Unit]

  def update(league: League): Future[Int]

  def delete(id: Int): Future[Int]
}

class LeagueRepositoryImpl @Inject()(database: DB) extends LeagueRepository {
  private val log = LoggerFactory.getLogger(this.getClass)

  val db: JdbcBackend#DatabaseDef = database.db

  lazy val leagues = League.table
  lazy val tasks = UpdateTask.table


  def filterQuery(id: Int): Query[LeaguesTable, League, Seq] = leagues.filter(_.id === id)

  def findById(id: Int): Future[Option[League]] = {
    val future: Future[Option[League]] = db.run(filterQuery(id).result.headOption)
    future.onFailure {
      case e => log.error("Couldn't find league by id", e)
    }
    future
  }


  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(league: League): Future[Int] = {
    db.run(leagues += league)
  }

  def insertLeagueTask(league: League): Future[Unit] = {
    exists(league.id).map(present =>
      if (!present) db.run(DBIO.seq(
        leagues += league,
        tasks += new UpdateTask(league.id.toLong, League.getClass.getSimpleName, 0.toByte)
      ).transactionally)
    )
  }

  def update(league: League): Future[Int] = {
    db.run(filterQuery(league.id).update(league))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)
}


