package com.eb.schedule.model.dao

import com.eb.schedule.model.slick.{UpdateTask, UpdateTasks}
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend
import slick.lifted.TableQuery

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */

trait UpdateTaskRepositoryComponent {

  def taskRepository: UpdateTaskRepository

  trait UpdateTaskRepository {
    def findByIdAndName(id: Long, classname: String): Future[UpdateTask]

    def exists(id: Long, classname: String): Future[Boolean]

    def insert(task: UpdateTask): Future[Int]

    def update(task: UpdateTask): Future[Int]

    def delete(id: Long, classname: String): Future[Int]

    def getPendingTasks(classname: String): Future[Seq[UpdateTask]]
  }
}

trait UpdateTaskRepositoryImplComponent extends UpdateTaskRepositoryComponent {
  val db: JdbcBackend#DatabaseDef

  def taskRepository = new UpdateTaskRepositoryImpl(db)

  class UpdateTaskRepositoryImpl(val db: JdbcBackend#DatabaseDef) extends UpdateTaskRepository {

    lazy val tasks = new TableQuery(tag => new UpdateTasks(tag))

    def filterQuery(id: Long, classname: String): Query[UpdateTasks, UpdateTask, Seq] = tasks.filter(t => t.id === id && t.classname === classname)

    def findByIdAndName(id: Long, classname: String): Future[UpdateTask] =
      try db.run(filterQuery(id, classname).result.head)
      finally db.close

    def exists(id: Long, classname: String): Future[Boolean] =
      try db.run(filterQuery(id, classname).exists.result)
      finally db.close

    def insert(task: UpdateTask): Future[Int] = {
      try db.run(tasks += task)
      finally db.close
    }

    def update(task: UpdateTask): Future[Int] = {
      try db.run(filterQuery(task.id, task.classname).update(task))
      finally db.close
    }

    def delete(id: Long, classname: String): Future[Int] =
      try db.run(filterQuery(id, classname).delete)
      finally db.close

    def getPendingTasks(classname: String): Future[Seq[UpdateTask]] = {
      try db.run(tasks.filter(t => t.classname === classname && t.result === 0.toByte).result)
      finally db.close
    }
  }

}

