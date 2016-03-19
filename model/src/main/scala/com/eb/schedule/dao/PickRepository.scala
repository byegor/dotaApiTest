package com.eb.schedule.model.dao

import com.eb.schedule.dto.PickDTO
import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by Egor on 13.02.2016.
  */

trait PickRepository {
  def findById(p: Pick): Future[Pick]

  def findByMatchId(id: Long): Future[Seq[Pick]]

  def exists(p: Pick): Future[Boolean]

  def insert(pick: Seq[Pick]): Future[Option[Int]]

  def insertIfNotExists(p: Pick): Unit

  def delete(p: Pick): Future[Int]
}

class PickRepositoryImpl @Inject()(database: DB) extends PickRepository {
  val db: JdbcBackend#DatabaseDef = database.db

  private lazy val picks = new TableQuery(tag => new Picks(tag))

  def filterQuery(p: Pick): Query[Picks, Pick, Seq] = picks.filter(e => e.matchId === p.matchId && e.heroId === p.heroId)

  def findByMatchId(matchId:Long): Future[Seq[Pick]] = {
    db.run(picks.filter(e => e.matchId === matchId).result)
  }

  def findById(p: Pick): Future[Pick] =
    db.run(filterQuery(p).result.head)

  def exists(p: Pick): Future[Boolean] =
    db.run(filterQuery(p).exists.result)

  def insert(pick: Seq[Pick]): Future[Option[Int]] = {
    db.run(picks ++= pick)
  }

  def insert(pick: Pick): Future[Int] = {
    db.run(picks += pick)
  }

  def insertIfNotExists(p: Pick): Unit = {
    val result: Boolean = Await.result(exists(p), Duration.Inf)
    if(!result) insert(p)
    exists(p).map(res => if(!res) insert(p))
  }

  def delete(p: Pick): Future[Int] =
    db.run(filterQuery(p).delete)
}