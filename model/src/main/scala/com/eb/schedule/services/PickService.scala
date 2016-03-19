package com.eb.schedule.model.services

import com.eb.schedule.dto.{DTOUtils, TeamDTO, PickDTO}
import com.eb.schedule.model.dao.PickRepository
import com.eb.schedule.model.slick.Pick
import com.google.inject.Inject

import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait PickService {
  def findById(p: PickDTO): Future[PickDTO]

  def findByMatchId(id: Long): Future[Seq[PickDTO]]

  def exists(p: PickDTO): Future[Boolean]

  def insert(pick: PickDTO): Future[Int]

  def insertIfNotExists(matchId:Long, t: TeamDTO, isRadiant: Boolean): Unit

  def delete(p: PickDTO): Future[Int]
}

class PickServiceImpl @Inject()(pickRep: PickRepository) extends PickService {
  def findById(p: PickDTO): Future[PickDTO] = {
//    pickRep.findById(p)
    null
  }

  def findByMatchId(id: Long): Future[Seq[PickDTO]] = {
    null
  }

  def exists(p: PickDTO): Future[Boolean] = {
//    pickRep.exists(p)
    null
  }

  def insert(pick: PickDTO): Future[Int] = {
    null
  }

  def insertIfNotExists(matchId:Long, t: TeamDTO, isRadiant: Boolean): Unit = {
    val radiantPicks: List[Pick] = DTOUtils.transformPickFromDTO(matchId, t, isRadiant)
    radiantPicks.foreach(pickRep.insertIfNotExists)
  }

  def delete(p: PickDTO): Future[Int] = {
    null
  }
}