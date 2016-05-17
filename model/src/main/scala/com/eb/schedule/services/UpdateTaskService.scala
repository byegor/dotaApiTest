package com.eb.schedule.model.services

import com.eb.schedule.dto.{LeagueDTO, TaskDTO}
import com.eb.schedule.model.dao.UpdateTaskRepository
import com.eb.schedule.model.slick.{League, Team, UpdateTask}
import com.eb.schedule.utils.DTOUtils
import com.google.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 20.02.2016.
  */
trait UpdateTaskService {
  def findByIdAndName(id: Long, classname: String): Future[UpdateTask]

  def exists(id: Long, classname: String): Future[Boolean]

  def insert(task: UpdateTask)

  def update(task: UpdateTask): Future[Int]

  def delete(id: Long, classname: String): Future[Int]

  def getPendingTeamTasks(): Future[Seq[TaskDTO]]

  def getPendingLeagueTasks(): Future[Seq[TaskDTO]]

}

class UpdateTaskServiceImpl @Inject()(taskRepository: UpdateTaskRepository) extends UpdateTaskService {
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

  def getPendingTeamTasks(): Future[Seq[TaskDTO]] = {
    getPendingTasks(Team.getClass.getSimpleName)
  }

  def getPendingLeagueTasks(): Future[Seq[TaskDTO]] = {
    getPendingTasks(League.getClass.getSimpleName)
  }


  private def getPendingTasks(classname: String): Future[Seq[TaskDTO]] = {
    taskRepository.getPendingTasks(classname).map(f => f.map(t => DTOUtils.createUpdateTaskDTO(t)))
  }
}