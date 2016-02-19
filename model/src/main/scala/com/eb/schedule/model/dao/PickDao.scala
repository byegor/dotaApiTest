package com.eb.schedule.model.dao

import com.eb.schedule.model.dao.TeamDao._
import com.eb.schedule.model.slick._
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Egor on 13.02.2016.
  */
object PickDao extends DBConf {


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
      if (!present){
         update(p)
      } else{
        insert(p)
      }
    }
  }

  def delete(p: Pick): Future[Int] =
    try db.run(filterQuery(p).delete)
    finally db.close
}
