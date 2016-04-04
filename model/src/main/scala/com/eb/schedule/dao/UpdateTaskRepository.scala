package com.eb.schedule.model.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.UpdateTask
import com.eb.schedule.model.slick.UpdateTask.UpdateTaskTable
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

/**
  * Created by Egor on 13.02.2016.
  */

trait UpdateTaskRepository {
  def findByIdAndName(id: Long, classname: String): Future[UpdateTask]

  def exists(id: Long, classname: String): Future[Boolean]

  def insert(task: UpdateTask): Future[Int]

  def update(task: UpdateTask): Future[Int]

  def delete(id: Long, classname: String): Future[Int]

  def getPendingTasks(classname: String): Future[Seq[UpdateTask]]
}

class UpdateTaskRepositoryImpl @Inject()(database: DB) extends UpdateTaskRepository {

  val db: JdbcBackend#DatabaseDef = database.db

  lazy val tasks = UpdateTask.table

  def filterQuery(id: Long, classname: String): Query[UpdateTaskTable, UpdateTask, Seq] = tasks.filter(t => t.id === id && t.classname === classname)

  def findByIdAndName(id: Long, classname: String): Future[UpdateTask] =
    db.run(filterQuery(id, classname).result.head)


  def exists(id: Long, classname: String): Future[Boolean] =
    db.run(filterQuery(id, classname).exists.result)


  def insert(task: UpdateTask): Future[Int] = {
    db.run(tasks += task)
  }

  def update(task: UpdateTask): Future[Int] = {
    db.run(filterQuery(task.id, task.classname).update(task))

  }

  def delete(id: Long, classname: String): Future[Int] =
    db.run(filterQuery(id, classname).delete)


  def getPendingTasks(classname: String): Future[Seq[UpdateTask]] = {
    db.run(tasks.filter(t => t.classname === classname && t.result === 0.toByte).result)
  }
}

