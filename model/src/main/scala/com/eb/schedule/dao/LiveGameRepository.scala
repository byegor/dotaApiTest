package com.eb.schedule.model.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.{LiveGame, LiveGames}
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
trait LiveGameRepository {
  def findById(id: Long): Future[LiveGame]

  def exists(id: Long): Future[Boolean]

  def insert(matchDetails: LiveGame): Future[Int]

  def update(liveGame: LiveGame): Future[Int]

  def delete(id: Long): Future[Int]
}

class LiveGameRepositoryImpl @Inject()(database: DB) extends LiveGameRepository {
  private val log = LoggerFactory.getLogger(this.getClass)
  val db: JdbcBackend#DatabaseDef = database.db

  lazy val liveGames = new TableQuery(tag => new LiveGames(tag))

  def filterQuery(id: Long): Query[LiveGames, LiveGame, Seq] = liveGames.filter(_.matchId === id)

  def findById(id: Long): Future[LiveGame] = {
    val future: Future[LiveGame] = db.run(filterQuery(id).result.head)
    future.onFailure{
      case e => log.error("couldn't get Live game by id", e)
    }
    future
  }

  def exists(id: Long): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(matchDetails: LiveGame): Future[Int] = {
    val future: Future[Int] = db.run(liveGames += matchDetails)
    future.onFailure{
      case e => log.error("couldn't insert Live game", e)
    }
    future
  }

  def update(liveGame: LiveGame): Future[Int] = {
    db.run(filterQuery(liveGame.matchId).update(liveGame))
  }

  def delete(id: Long): Future[Int] =
    db.run(filterQuery(id).delete)
}


