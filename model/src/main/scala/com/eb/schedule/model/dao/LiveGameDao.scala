package com.eb.schedule.model.dao

import com.eb.schedule.model.slick.{LiveGame, LiveGames}
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
trait LiveGameRepComp {

  def repository: LiveGameRep

  trait LiveGameRep {
    def findById(id: Int): Future[LiveGame]

    def exists(id: Long): Future[Boolean]

    def insert(matchDetails: LiveGame): Future[Int]

    def update(id: Long, matchDetails: LiveGame): Future[Int]

    def delete(id: Long): Future[Int]
  }
}

trait LiveGameRepImplComp extends LiveGameRepComp{
  val db: JdbcBackend#DatabaseDef

  def repository = new LiveGameRepImpl(db)

  class LiveGameRepImpl(val db: JdbcBackend#DatabaseDef) extends LiveGameRep{

    lazy val liveGames = new TableQuery(tag => new LiveGames(tag))

    def filterQuery(id: Long): Query[LiveGames, LiveGame, Seq] = liveGames.filter(_.matchId === id)

    def findById(id: Int): Future[LiveGame] =
      try db.run(filterQuery(id).result.head)
      finally db.close

    def exists(id: Long): Future[Boolean] =
      try db.run(filterQuery(id).exists.result)
      finally db.close

    def insert(matchDetails: LiveGame): Future[Int] = {
      try db.run(liveGames += matchDetails)
      finally db.close
    }

    def update(id: Long, matchDetails: LiveGame): Future[Int] = {
      try db.run(filterQuery(id).update(matchDetails))
      finally db.close
    }

    def delete(id: Long): Future[Int] =
      try db.run(filterQuery(id).delete)
      finally db.close
  }

}
