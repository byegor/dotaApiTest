package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.dto.ItemDTO
import com.eb.schedule.services.ItemService
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory


class ItemsCrawler @Inject()(itemService: ItemService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    try {
      val steamItems: JsonArray = getItemsInfoFromSteam()
      var items: List[ItemDTO] = Nil
      for (i <- 0 until steamItems.size()) {
        val itemJson: JsonObject = steamItems.get(i).getAsJsonObject
        items ::= new ItemDTO(itemJson.get("id").getAsInt, parseName(itemJson.get("name").getAsString))
      }
      itemService.insert(items)
    }catch {
      case e: Throwable => log.error("", e)
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
