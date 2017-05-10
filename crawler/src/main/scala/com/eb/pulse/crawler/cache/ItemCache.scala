package com.eb.pulse.crawler.cache

import com.eb.pulse.crawler.service.ItemService
import com.eb.schedule.model.slick.Item
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class ItemCache(val itemService: ItemService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val cache: mutable.Map[Int, Item] = {
    val items: Seq[Item] = Await.result(itemService.findAll(), Duration.Inf)
    val itemMap: mutable.Map[Int, Item] = new mutable.HashMap[Int, Item]()
    items.foreach(h => itemMap.put(h.id, h))
    itemMap
  }

  def getItem(id: Int): Item = {
    if (id != 0) {
      val option: Option[Item] = cache.get(id)
      if (option.isDefined) {
        option.get
      } else {
        log.error("ALARMAAAA    Couldn't find an item: " + id)
        new Item(id, "")
        //todo item task
      }
    } else {
      new Item(id, "")
    }
  }


}
