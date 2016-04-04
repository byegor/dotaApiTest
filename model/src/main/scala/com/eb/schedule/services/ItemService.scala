package com.eb.schedule.services

import com.eb.schedule.dao.{ItemRepository, HeroRepository}
import com.eb.schedule.dto.{ItemDTO, HeroDTO}
import com.eb.schedule.model.slick.Item
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 20.02.2016.
  */
trait ItemService {
  def findById(id: Int): Future[ItemDTO]

  def exists(id: Int): Future[Boolean]

  def findAll(): Future[Seq[ItemDTO]]

  def insert(items: Seq[ItemDTO])
}

class ItemServiceImpl @Inject()(itemRep: ItemRepository) extends ItemService {
  def findById(id: Int): Future[ItemDTO] = {
    itemRep.findById(id).map(h => new ItemDTO(h.id, h.name))
  }

  def exists(id: Int): Future[Boolean] = {
    itemRep.exists(id)
  }

  override def findAll(): Future[Seq[ItemDTO]] = {
    itemRep.findAll().map(seq => seq.map(h => new ItemDTO(h.id, h.name)))
  }

  def insert(items: Seq[ItemDTO]) = {
    val itemList: Seq[Item] = items.map(i => new Item(i.id, i.name))
    itemRep.insert(itemList)
  }
}
