package com.eb.schedule.model.dao

import com.eb.schedule.model.dao.LeagueDao._
import com.eb.schedule.model.slick._
import slick.lifted.TableQuery
import scala.concurrent.Future
import slick.driver.MySQLDriver.api._

/**
  * Created by Egor on 13.02.2016.
  */
object TeamDao extends DBConf {


  private lazy val teams = new TableQuery(tag => new Teams(tag))
  private lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))


  def filterQuery(id: Int): Query[Teams, Team, Seq] = teams.filter(_.id === id)

  def findById(id: Int): Future[Team] =
    try db.run(filterQuery(id).result.head)
    finally db.close

  def exists(id: Int): Future[Boolean] =
    try db.run(filterQuery(id).exists.result)
    finally db.close

  def insert(team: Team): Future[Int] = {
    try db.run(teams += team)
    finally db.close
  }

  def insertIfNotExists(id: Int) = teams.forceInsertQuery {
    val exists = filterQuery(id).exists
    val insert = (id.bind, "") <>(Team.apply _ tupled, Team.unapply)
    for (team <- Query(insert) if !exists) yield team
  }

  def insertTeamTask(id: Int) = {
    exists(id).onSuccess { case present =>
      if (!present) try db.run(DBIO.seq(
        teams += new Team(id, "", ""),
        tasks += new UpdateTask(id.toLong, Team.getClass.getSimpleName, 0.toByte)
      ).transactionally) finally db.close
    }
  }

  def saveOrUpdateTeamAndTask(team: Team) = {
    exists(team.id).onSuccess { case present =>
      if (present) try db.run(DBIO.seq(
        filterQuery(team.id).update(team),
        UpdateTaskDao.filterQuery(team.id, Team.getClass.getSimpleName).update(new UpdateTask(team.id, Team.getClass.getSimpleName, 1))
      ).transactionally) finally db.close
    }
  }

  def update(team: Team): Future[Int] = {
    try db.run(filterQuery(team.id).update(team))
    finally db.close
  }

  def delete(id: Int): Future[Int] =
    try db.run(filterQuery(id).delete)
    finally db.close
}
