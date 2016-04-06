package com.eb.schedule.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.{Item, NetWorth}
import com.eb.schedule.model.slick.Item.ItemTable
import com.eb.schedule.model.slick.NetWorth.NetWorthTable
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

/**
  * Created by Egor on 26.03.2016.
  */
trait NetWorthRepository {

  def findByMatchId(id: Long): Future[Option[NetWorth]]

  def exists(mathcId: Long): Future[Boolean]

  def update(nw: NetWorth): Future[Int]

  def insert(nw: NetWorth): Future[Int]
}

class NetWorthRepositoryImpl @Inject()(database: DB) extends NetWorthRepository {

  private val log = LoggerFactory.getLogger(this.getClass)

  val db: JdbcBackend#DatabaseDef = database.db

  lazy val netWorth = NetWorth.table

  def filterQuery(id: Long): Query[NetWorthTable, NetWorth, Seq] = netWorth.filter(_.matchId === id)

  def findByMatchId(id: Long): Future[Option[NetWorth]] =
    db.run(filterQuery(id).result.headOption)

  def exists(matchId: Long): Future[Boolean] = {
    db.run(filterQuery(matchId).exists.result)
  }

  def update(nw: NetWorth): Future[Int] = {
    db.run(filterQuery(nw.matchId).update(nw))
  }

  def insert(nw: NetWorth): Future[Int] = {
    db.run(netWorth += nw)
  }

}
