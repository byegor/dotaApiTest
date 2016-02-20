package com.eb.schedule.model.dao


import com.eb.schedule.model.slick._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery
import scala.concurrent.{ExecutionContext, Future}
import slick.driver.MySQLDriver.api._
import ExecutionContext.Implicits.global

/**
  * Created by Egor on 13.02.2016.
  */


trait TeamRepositoryComponent{

  def teamRepository:TeamRepository

  trait TeamRepository{
    def findById(id: Int): Future[Team]

    def exists(id: Int): Future[Boolean]

    def insert(team: Team): Future[Int]

    def insertTeamTask(id: Int)

    def update(team: Team): Future[Int]

    def delete(id: Int): Future[Int]
  }
}

trait TeamRepositoryImplComponent extends TeamRepositoryComponent{
  val db:JdbcBackend#DatabaseDef

  def teamRepository = new TeamRepositoryImpl(db)

  class TeamRepositoryImpl(val db:JdbcBackend#DatabaseDef) extends TeamRepository {
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
}

