package com.eb.schedule.model.services

import com.eb.schedule.model.dao.PickRepository
import com.eb.schedule.model.slick.Pick
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait PickService {
  def findById(p: Pick): Future[Pick]

  def exists(p: Pick): Future[Boolean]

  def insert(pick: Pick): Future[Int]

  def update(p: Pick): Future[Int]

  def updateOrCreate(p: Pick): Unit

  def delete(p: Pick): Future[Int]
}

class PickServiceImpl @Inject()(pickRep: PickRepository) extends PickService {
  def findById(p: Pick): Future[Pick] = {
    pickRep.findById(p)
  }

  def exists(p: Pick): Future[Boolean] = {
    pickRep.exists(p)
  }

  def insert(pick: Pick): Future[Int] = {
    pickRep.insert(pick)
  }

  def update(p: Pick): Future[Int] = {
    pickRep.update(p)
  }

  def updateOrCreate(p: Pick): Unit = {
    pickRep.updateOrCreate(p)
  }

  def delete(p: Pick): Future[Int] = {
    pickRep.delete(p)
  }
}