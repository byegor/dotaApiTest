package com.eb.schedule.model.dao


import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick._
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */


trait TeamRepository {
  def findById(id: Int): Future[Option[Team]]

  def exists(id: Int): Future[Boolean]

  def insert(team: Team): Future[Int]

  def insertTeamTask(team: Team):Future[Unit]

  def saveOrUpdateTeamAndTask(team: Team)

  def update(team: Team): Future[Int]

  def delete(id: Int): Future[Int]
}


class TeamRepositoryImpl @Inject()(database: DB) extends TeamRepository {

  private val log = LoggerFactory.getLogger(this.getClass)

  val db: JdbcBackend#DatabaseDef = database.db

  private lazy val teams = new TableQuery(tag => new Teams(tag))
  private lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))


  def filterQuery(id: Int): Query[Teams, Team, Seq] = teams.filter(_.id === id)

  def findById(id: Int): Future[Option[Team]] = {
    val future: Future[Option[Team]] = db.run(filterQuery(id).result.headOption)
    future.onFailure{
      case e => log.error("Couldn't find team by id", e)
    }
    future
  }


  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)


  def insert(team: Team): Future[Int] = {
    db.run(teams += team)
  }

  def insertTeamTask(team: Team):Future[Unit] = {
    exists(team.id).map(present => if(!present){
      val run: Future[Unit] = db.run(DBIO.seq(
        teams += team,
        tasks += new UpdateTask(team.id.toLong, Team.getClass.getSimpleName, 0.toByte)
      ).transactionally)
      run
    })
  }

  def saveOrUpdateTeamAndTask(team: Team) = {
    exists(team.id).onSuccess { case present =>
      if (present){
        db.run(DBIO.seq(
          filterQuery(team.id).update(team),
          tasks.filter(t => t.id === team.id.toLong && t.classname === Team.getClass.getSimpleName).update(new UpdateTask(team.id.toLong, Team.getClass.getSimpleName, 1))
        ).transactionally)
      } else{
        db.run(DBIO.seq(
          teams +=team,
          tasks.filter(t => t.id === team.id.toLong && t.classname === Team.getClass.getSimpleName).update(new UpdateTask(team.id.toLong, Team.getClass.getSimpleName, 1))
        ).transactionally)
      }
    }
  }

  def update(team: Team): Future[Int] = {
    db.run(filterQuery(team.id).update(team))
  }

  def delete(id: Int): Future[Int] =
    db.run(filterQuery(id).delete)
}

