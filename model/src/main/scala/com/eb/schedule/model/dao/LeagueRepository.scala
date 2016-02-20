package com.eb.schedule.model.dao

import com.eb.schedule.model.slick._
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */

trait LeagueRepComp {
  def leagueRep: LeagueRep

  trait LeagueRep {

    def findById(id: Int): Future[League]

    def exists(id: Int): Future[Boolean]

    def insert(league: League): Future[Int]

    def insertLeagueTask(id: Int)

    def update(id: Int, league: League): Future[Int]

    def delete(id: Int): Future[Int]
  }

}

trait LeagueRepImpComp extends LeagueRepComp {
  val db: JdbcBackend#DatabaseDef

  def leagueRep = new LeagueRepImpl(db)

  class LeagueRepImpl(val db: JdbcBackend#DatabaseDef) extends LeagueRep {

    lazy val leagues = new TableQuery(tag => new Leagues(tag))
    lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))


    def filterQuery(id: Int): Query[Leagues, League, Seq] = leagues.filter(_.id === id)

    def findById(id: Int): Future[League] =
      try db.run(filterQuery(id).result.head)
      finally db.close

    def exists(id: Int): Future[Boolean] =
      try db.run(filterQuery(id).exists.result)
      finally db.close

    def insert(league: League): Future[Int] = {
      try db.run(leagues += league)
      finally db.close
    }

    def insertLeagueTask(id: Int) = {
      exists(id).onSuccess { case present =>
        if (!present) try db.run(DBIO.seq(
          leagues += new League(id, ""),
          tasks += new UpdateTask(id.toLong, League.getClass.getSimpleName, 0.toByte)
        ).transactionally) finally db.close
      }
    }

    def update(id: Int, league: League): Future[Int] = {
      try db.run(filterQuery(id).update(league))
      finally db.close
    }

    def delete(id: Int): Future[Int] =
      try db.run(filterQuery(id).delete)
      finally db.close
  }

}

