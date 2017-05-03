package com.eb.pulse.crawler.service

import com.eb.schedule.model.dao.UpdateTaskRepository
import com.eb.schedule.model.slick.{League, Team, UpdateTask}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TaskService (taskRepository: UpdateTaskRepository)  {
  def findByIdAndName(id: Long, classname: String): Future[UpdateTask] = {
    taskRepository.findByIdAndName(id, classname)
  }

  def exists(id: Long, classname: String): Future[Boolean] = {
    taskRepository.exists(id, classname)
  }

  def insert(task: UpdateTask) = {
    taskRepository.exists(task.id, task.classname).onSuccess{
      case exists => if(!exists) taskRepository.insert(task)
    }
  }

  def update(task: UpdateTask): Future[Int] = {
    taskRepository.update(task)
  }

  def delete(id: Long, classname: String): Future[Int] = {
    taskRepository.delete(id, classname)
  }

  def getPendingTeamTasks(): Future[Seq[UpdateTask]] = {
    getPendingTasks(Team.getClass.getSimpleName)
  }

  def getPendingLeagueTasks(): Future[Seq[UpdateTask]] = {
    getPendingTasks(League.getClass.getSimpleName)
  }


  private def getPendingTasks(classname: String): Future[Seq[UpdateTask]] = {
    taskRepository.getPendingTasks(classname)
  }
}