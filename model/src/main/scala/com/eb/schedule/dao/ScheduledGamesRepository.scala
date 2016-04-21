package com.eb.schedule.model.dao

import com.eb.schedule.dto.CurrentGameDTO
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.ScheduledGame.ScheduledGameTable
import com.eb.schedule.model.slick._
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
trait ScheduledGameRepository {
  def findById(id: Int): Future[ScheduledGame]

  /*def findByMatchId(matchId: Long): Future[Option[ScheduledGame]]*/

  def exists(id: Int): Future[Boolean]

  def insert(game: ScheduledGame): Future[Int]

  def insertAndGet(game: ScheduledGame): Future[Int]

  def update(game: ScheduledGame): Future[Int]

  def updateStatus(id: Int, status: Byte): Future[Int]

  /*def updateStatusByMatchId(id: Long, status: Byte): Future[Int]*/

  def delete(id: Int): Future[Int]

  def getScheduledGames(team1: Int, team2: Int, league: Int, matchStatus: MatchStatus): Future[Option[ScheduledGame]]
}

class ScheduledGameRepositoryImpl @Inject()(val database: DB) extends ScheduledGameRepository {
  private val log = LoggerFactory.getLogger(this.getClass)

  val db = database.db
  lazy val games = ScheduledGame.table

  def filterQuery(id: Int): Query[ScheduledGameTable, ScheduledGame, Seq] = games.filter(_.id === id)

  def findById(id: Int): Future[ScheduledGame] =
    db.run(filterQuery(id).result.head)

  /*def findByMatchId(matchId: Long): Future[Option[ScheduledGame]] =
    db.run(games.filter(_.matchId === matchId).result.headOption)*/

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(game: ScheduledGame): Future[Int] = {
    val future: Future[Int] = db.run(games += game)
    future.onFailure {
      case e =>
        log.error("couldn't insert scheduled game", e)
        throw e
    }
    future
  }

  //val userId =  (users returning users.map(_.id)) += User(None, "Stefan", "Zeiger")
  def insertAndGet(game: ScheduledGame): Future[Int] = {
    val insertQuery = games returning games.map(_.id)
    val action = insertQuery += game
    val future: Future[Int] = db.run(action)
    future.onFailure {
      case e =>
        log.error("couldn't insertAndGet scheduled game", e)
        throw e
    }
    future
  }

  def update(game: ScheduledGame): Future[Int] = {
    db.run(filterQuery(game.id).update(game))
  }

  def updateStatus(id: Int, status: Byte): Future[Int] = {
    db.run(games
      .filter(_.id === id)
      .map(x => x.status)
      .update(status))
  }

  /*def updateStatusByMatchId(id: Long, status: Byte): Future[Int] = {
    db.run(games
      .filter(g => g.matchId === id)
      .map(x => x.status)
      .update(status))
  }*/

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)


  private def getScheduledGameQuery(team1: Int, team2: Int, league: Int, matchStatus: MatchStatus) = {
    games.filter(g => g.status === matchStatus.status && g.leagueId === league && ((g.radiant === team1 && g.dire === team2) || (g.radiant === team2 && g.dire === team1)))
      .sortBy(_.startDate)
  }

  def getScheduledGames(team1: Int, team2: Int, league: Int, matchStatus: MatchStatus): Future[Option[ScheduledGame]] = {
    db.run(getScheduledGameQuery(team1, team2, league, matchStatus).result.headOption)
  }
}

