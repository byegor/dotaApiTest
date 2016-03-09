package com.eb.schedule.model.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */

trait LeagueRepository {

  def findById(id: Int): Future[League]

  def exists(id: Int): Future[Boolean]

  def insert(league: League): Future[Int]

  def insertLeagueTask(id: Int)

  def update(id: Int, league: League): Future[Int]

  def delete(id: Int): Future[Int]
}

class LeagueRepositoryImpl @Inject()(database: DB) extends LeagueRepository {
  val db: JdbcBackend#DatabaseDef = database.db

  lazy val leagues = new TableQuery(tag => new Leagues(tag))
  lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))


  def filterQuery(id: Int): Query[Leagues, League, Seq] = leagues.filter(_.id === id)

  def findById(id: Int): Future[League] =
    db.run(filterQuery(id).result.head)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(league: League): Future[Int] = {
    db.run(leagues += league)
  }

  def insertLeagueTask(id: Int) = {
    exists(id).onSuccess { case present =>
      if (!present) db.run(DBIO.seq(
        leagues += new League(id, ""),
        tasks += new UpdateTask(id.toLong, League.getClass.getSimpleName, 0.toByte)
      ).transactionally)
    }
  }

  def update(id: Int, league: League): Future[Int] = {
    db.run(filterQuery(id).update(league))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)
}


