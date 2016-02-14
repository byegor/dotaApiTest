package com.eb.schedule.model.dao

import com.eb.schedule.model.slick._
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
object GameDao extends DBConf {


  lazy val games = new TableQuery(tag => new Games(tag))


  def filterQuery(id: Int): Query[Games, Game, Seq] = games.filter(_.id === id)

  def findById(id: Int): Future[Game] =
    try db.run(filterQuery(id).result.head)
    finally db.close

  def exists(id: Int): Future[Boolean] =
    try db.run(filterQuery(id).exists.result)
    finally db.close

  def insert(game: Game): Future[Int] = {
    try db.run(games += game)
    finally db.close
  }

  def update(id: Int, game: Game): Future[Int] = {
    try db.run(filterQuery(id).update(game))
    finally db.close
  }

  def delete(id: Int): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close


  private def getUnfinishedGameQuery(matchDetails: MatchDetails) = {
    games.filter(g => (g.matchId === matchDetails.matchId)
      || (g.leagueId === matchDetails.leagueId && ((g.radiant === matchDetails.radiant && g.dire === matchDetails.dire) || (g.radiant === matchDetails.dire && g.dire === matchDetails.radiant))))
  }

  def getUnfinishedGames(matchDetails: MatchDetails):Future[Seq[Game]] = {
    try db.run(getUnfinishedGameQuery(matchDetails).result)
    finally db.close
  }
}
