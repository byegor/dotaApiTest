package com.eb.schedule.model.dao


import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */


trait TeamRepository {
  def findById(id: Int): Future[Team]

  def exists(id: Int): Future[Boolean]

  def insert(team: Team): Future[Int]

  def insertTeamTask(id: Int)

  def saveOrUpdateTeamAndTask(team: Team)

  def update(team: Team): Future[Int]

  def delete(id: Int): Future[Int]
}


class TeamRepositoryImpl @Inject()(database: DB) extends TeamRepository {

  val db: JdbcBackend#DatabaseDef = database.db

  private lazy val teams = new TableQuery(tag => new Teams(tag))
  private lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))


  def filterQuery(id: Int): Query[Teams, Team, Seq] = teams.filter(_.id === id)

  def findById(id: Int): Future[Team] =
    db.run(filterQuery(id).result.head)


  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)


  def insert(team: Team): Future[Int] = {
    db.run(teams += team)
  }

  def insertTeamTask(id: Int) = {
    exists(id).onSuccess { case present =>
      if (!present) db.run(DBIO.seq(
        teams += new Team(id, "", ""),
        tasks += new UpdateTask(id.toLong, Team.getClass.getSimpleName, 0.toByte)
      ).transactionally)
    }
  }

  def saveOrUpdateTeamAndTask(team: Team) = {
    exists(team.id).onSuccess { case present =>
      if (present) db.run(DBIO.seq(
        filterQuery(team.id).update(team),
        tasks.filter(t => t.id === team.id.toLong && t.classname === Team.getClass.getSimpleName).update(new UpdateTask(team.id.toLong, Team.getClass.getSimpleName, 1))
      ).transactionally)
    }
  }

  def update(team: Team): Future[Int] = {
    db.run(filterQuery(team.id).update(team))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)
}

