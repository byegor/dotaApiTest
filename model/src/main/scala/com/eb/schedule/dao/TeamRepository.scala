package com.eb.schedule.model.dao


import com.eb.schedule.model.slick.Team.TeamsTable
import com.eb.schedule.model.slick._
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */


trait TeamRepository {
  def findById(id: Int): Future[Option[Team]]

  def exists(id: Int): Future[Boolean]

  def insert(team: Team): Future[Int]

  def upsert(team: Team): Future[Unit]

  def update(team: Team): Future[Int]

  def delete(id: Int): Future[Int]
}


class TeamRepositoryImpl (implicit db: JdbcBackend#DatabaseDef) extends TeamRepository {

  private val log = LoggerFactory.getLogger(this.getClass)

  private lazy val teams = Team.table

  def filterQuery(id: Int): Query[TeamsTable, Team, Seq] = teams.filter(_.id === id)

  def findById(id: Int): Future[Option[Team]] = {
    val future: Future[Option[Team]] = db.run(filterQuery(id).result.headOption)
    future.onFailure {
      case e => log.error("Couldn't find team by id", e)
    }
    future
  }

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def insert(team: Team): Future[Int] = {
    db.run(teams += team)
  }

  def upsert(team: Team): Future[Unit] = {
    exists(team.id).map(present =>
      if (present) {
        db.run(DBIO.seq(
          teams.filter(t => t.id === team.id).update(team)
        ).transactionally)
      } else {
        db.run(DBIO.seq(
          teams += team
        ).transactionally)
      })
  }

  def update(team: Team): Future[Int] = {
    db.run(filterQuery(team.id).update(team))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)
}

