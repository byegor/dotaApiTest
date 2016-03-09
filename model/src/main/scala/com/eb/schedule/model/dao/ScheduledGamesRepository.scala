package com.eb.schedule.model.dao

import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
trait ScheduledGameRepository {
  def findById(id: Int): Future[ScheduledGame]

  def findByMatchId(matchId: Long): Future[ScheduledGame]

  def exists(id: Int): Future[Boolean]

  def insert(game: ScheduledGame): Future[Int]

  def update(game: ScheduledGame): Future[Int]

  def updateStatus(id: Int, status: Byte): Future[Int]

  def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int]

  def delete(id: Int): Future[Int]

  def getScheduledGames(matchDetails: LiveGame): Future[ScheduledGame]
}

class ScheduledGameRepositoryImpl @Inject()(val database: DB) extends ScheduledGameRepository {
  val db = database.db
  lazy val games = new TableQuery(tag => new ScheduledGames(tag))

  def filterQuery(id: Int): Query[ScheduledGames, ScheduledGame, Seq] = games.filter(_.id === id)

  def findById(id: Int): Future[ScheduledGame] =
    db.run(filterQuery(id).result.head)

  def findByMatchId(matchId: Long): Future[ScheduledGame] =
    db.run(games.filter(_.matchId === matchId).result.head)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(game: ScheduledGame): Future[Int] = {
    db.run(games += game)
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

  def updateScore(matchId: Long, radiantScore: Byte, direScore: Byte): Future[Int] = {
    db.run(games
      .filter(_.matchId === matchId)
      .map(x => (x.radiantScore, x.direScore))
      .update((radiantScore, direScore)))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)


  private def getScheduledGameQuery(liveGame: LiveGame) = {
    games.filter(g => g.status === MatchStatus.SCHEDULED.status && g.leagueId === liveGame.leagueId && ((g.radiant === liveGame.radiant && g.dire === liveGame.dire) || (g.radiant === liveGame.dire && g.dire === liveGame.radiant)))
      .sortBy(_.startDate)
  }

  def getScheduledGames(matchDetails: LiveGame): Future[ScheduledGame] = {
    db.run(getScheduledGameQuery(matchDetails).result.head)
  }
}

