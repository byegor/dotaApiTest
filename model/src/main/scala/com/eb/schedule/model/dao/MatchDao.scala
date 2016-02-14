package com.eb.schedule.model.dao

import com.eb.schedule.model.slick.{MatchDetails, MatchDetailsTable, MatchDetails, MatchDetailss}
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */
object MatchDao extends DBConf {


  lazy val matches = new TableQuery(tag => new MatchDetailsTable(tag))


  def filterQuery(id: Long): Query[MatchDetailsTable, MatchDetails, Seq] = matches.filter(_.matchId === id)

  def findById(id: Int): Future[MatchDetails] =
    try db.run(filterQuery(id).result.head)
    finally db.close

  def exists(id: Long): Future[Boolean] =
    try db.run(filterQuery(id).exists.result)
    finally db.close

  def insert(matchDetails: MatchDetails): Future[Int] = {
    try db.run(matches += matchDetails)
    finally db.close
  }

  def update(id: Long, matchDetails: MatchDetails): Future[Int] = {
    try db.run(filterQuery(id).update(matchDetails))
    finally db.close
  }

  def delete(id: Long): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close
}
