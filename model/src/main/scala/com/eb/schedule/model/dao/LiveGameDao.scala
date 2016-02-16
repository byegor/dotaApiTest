package com.eb.schedule.model.dao

import com.eb.schedule.model.slick.{LiveGame, LiveGames}
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
object LiveGameDao extends DBConf {


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
