package com.eb.schedule.model.services

import com.eb.schedule.model.dao.{UpdateTaskRepositoryComponent, TeamRepositoryComponent}
import com.eb.schedule.model.slick.{UpdateTask, Team}

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait UpdateTaskServiceComponent {

  def taskService:UpdateTaskService

  trait UpdateTaskService{
    def findByIdAndName(id: Long, classname: String): Future[UpdateTask]

    def exists(id: Long, classname: String): Future[Boolean]

    def insert(task: UpdateTask): Future[Int]

    def update(task: UpdateTask): Future[Int]

    def delete(id: Long, classname: String): Future[Int]

    def getPendingTasks(classname: String): Future[Seq[UpdateTask]]
  }
}

trait UpdateTaskServiceImplComponent extends UpdateTaskServiceComponent{
  this: UpdateTaskRepositoryComponent =>

  def taskService = new UpdateTaskServiceImpl

  class UpdateTaskServiceImpl extends UpdateTaskService{
    def findByIdAndName(id: Long, classname: String): Future[UpdateTask]={
      taskRepository.findByIdAndName(id, classname)
    }

    def exists(id: Long, classname: String): Future[Boolean] = {
      taskRepository.exists(id, classname)
    }

    def insert(task: UpdateTask): Future[Int] = {
      taskRepository.insert(task)
    }

    def update(task: UpdateTask): Future[Int] = {
      taskRepository.update(task)
    }

    def delete(id: Long, classname: String): Future[Int] = {
      taskRepository.delete(id, classname)
    }

    def getPendingTasks(classname: String): Future[Seq[UpdateTask]] = {
      taskRepository.getPendingTasks(classname)
    }
  }
}