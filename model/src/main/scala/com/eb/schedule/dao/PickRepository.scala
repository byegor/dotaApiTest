package com.eb.schedule.model.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Egor on 13.02.2016.
  */

trait PickRepository {
  def findById(p: Pick): Future[Pick]

  def exists(p: Pick): Future[Boolean]

  def insert(pick: Pick): Future[Int]

  def update(p: Pick): Future[Int]

  def updateOrCreate(p: Pick): Unit

  def delete(p: Pick): Future[Int]
}

class PickRepositoryImpl @Inject()(database: DB) extends PickRepository {
  val db: JdbcBackend#DatabaseDef = database.db

  private lazy val picks = new TableQuery(tag => new Picks(tag))

  def filterQuery(p: Pick): Query[Picks, Pick, Seq] = picks.filter(e => e.matchId === p.matchId && e.pick === p.pick && e.radiant === p.radiant)

  def findById(p: Pick): Future[Pick] =
    db.run(filterQuery(p).result.head)

  def exists(p: Pick): Future[Boolean] =
    db.run(filterQuery(p).exists.result)

  def insert(pick: Pick): Future[Int] = {
    db.run(picks += pick)
  }

  def update(p: Pick): Future[Int] = {
    db.run(picks.filter(e => e.matchId === p.matchId && e.pick === p.pick && e.radiant === p.radiant).update(p))
  }

  def updateOrCreate(p: Pick): Unit = {
    exists(p).onSuccess { case present =>
      if (!present) {
        update(p)
      } else {
        insert(p)
      }
    }
  }

  def delete(p: Pick): Future[Int] =
    db.run(filterQuery(p).delete)
}