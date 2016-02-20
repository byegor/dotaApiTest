package com.eb.schedule.model.dao

import com.eb.schedule.model.slick._
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Egor on 13.02.2016.
  */

trait PickRepComp {

  def pickRep: PickRep

  trait PickRep {
    def findById(p: Pick): Future[Pick]

    def exists(p: Pick): Future[Boolean]

    def insert(pick: Pick): Future[Int]

    def update(p: Pick): Future[Int]

    def updateOrCreate(p: Pick): Unit

    def delete(p: Pick): Future[Int]
  }

}

trait PickRepImplComp extends PickRepComp {
  val db: JdbcBackend#DatabaseDef

  def pickRep = new PickRepImpl(db)

  class PickRepImpl(val db: JdbcBackend#DatabaseDef) extends PickRep {

    private lazy val picks = new TableQuery(tag => new Picks(tag))

    def filterQuery(p: Pick): Query[Picks, Pick, Seq] = picks.filter(e => e.matchId === p.matchId && e.pick === p.pick && e.radiant === p.radiant)

    def findById(p: Pick): Future[Pick] =
      try db.run(filterQuery(p).result.head)
      finally db.close

    def exists(p: Pick): Future[Boolean] =
      try db.run(filterQuery(p).exists.result)
      finally db.close

    def insert(pick: Pick): Future[Int] = {
      try db.run(picks += pick)
      finally db.close
    }

    def update(p: Pick): Future[Int] = {
      try db.run(picks.filter(e => e.matchId === p.matchId && e.pick === p.pick && e.radiant === p.radiant).update(p))
      finally db.close
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
      try db.run(filterQuery(p).delete)
      finally db.close
  }

}


