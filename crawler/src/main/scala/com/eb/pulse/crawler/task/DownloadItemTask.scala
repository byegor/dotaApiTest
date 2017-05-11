package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.CrawlerUrls._
import com.eb.pulse.crawler.service.ItemService
import com.eb.schedule.model.slick.Item
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory


class DownloadItemTask(itemService: ItemService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    try {
      val steamItems: JsonArray = getItemsInfoFromSteam()
      var items: List[Item] = Nil
      for (i <- 0 until steamItems.size()) {
        val itemJson: JsonObject = steamItems.get(i).getAsJsonObject
        items ::= Item(itemJson.get("id").getAsInt, parseName(itemJson.get("name").getAsString))
      }
      itemService.insert(items)
    }catch {
      case e: Throwable => log.error("Issue in running task", e)
    }
  }

  def parseName(name: String): String = {
    name.replace("item_", "").replace("_", " ")
  }


  def getItemsInfoFromSteam(): JsonArray = {
    val teamInfo: JsonObject = httpUtils.getResponseAsJson(GET_ITEMS)
    val result: JsonObject = teamInfo.getAsJsonObject("result")
    val items: JsonArray = result.getAsJsonArray("items")
    items
  }

}
