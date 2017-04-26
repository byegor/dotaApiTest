package com.eb.pulse.crawler.service

import com.eb.schedule.dao.ItemRepository
import com.eb.schedule.dto.ItemDTO
import com.eb.schedule.model.slick.Item

import scala.concurrent.Future

/**
  * Created by Iegor.Bondarenko on 26.04.2017.
  */
class ItemService(itemRepository: ItemRepository) {

  def insert(items: List[Item])={
    itemRepository.insert(items)
  }
}