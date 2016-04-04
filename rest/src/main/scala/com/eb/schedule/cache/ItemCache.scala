package com.eb.schedule.cache

import com.eb.schedule.dto.ItemDTO
import com.eb.schedule.services.ItemService
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class ItemCache @Inject()(val itemService: ItemService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val unknownItem: ItemDTO = new ItemDTO(-1)

  private val cache: mutable.Map[Int, ItemDTO] = {
    val items: Seq[ItemDTO] = Await.result(itemService.findAll(), Duration.Inf)
    val itemMap: mutable.Map[Int, ItemDTO] = new mutable.HashMap[Int, ItemDTO]()
    items.foreach(h => itemMap.put(h.id, h))
    itemMap
  }

  def getItem(id: Int): ItemDTO = {
    val option: Option[ItemDTO] = cache.get(id)
    if (option.isDefined) {
      option.get
    } else {
      log.error("Couldn't find an item: " + id)
      unknownItem
    }
  }


}
