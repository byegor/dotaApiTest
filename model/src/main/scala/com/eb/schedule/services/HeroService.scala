package com.eb.schedule.services

import com.eb.schedule.dao.HeroRepository
import com.eb.schedule.dto.HeroDTO
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait HeroService {
  def findById(id: Int): Future[HeroDTO]

  def exists(id: Int): Future[Boolean]

  def findAll(): Future[Seq[HeroDTO]]

}

class HeroServiceImpl @Inject()(heroRep: HeroRepository) extends HeroService {
  def findById(id: Int): Future[HeroDTO] = {
    heroRep.findById(id).map(h => new HeroDTO(h.id, h.name))
  }

  def exists(id: Int): Future[Boolean] = {
    heroRep.exists(id)
  }

  override def findAll(): Future[Seq[HeroDTO]] = {
    heroRep.findAll().map(seq => seq.map(h => new HeroDTO(h.id, h.name)))
  }
}
