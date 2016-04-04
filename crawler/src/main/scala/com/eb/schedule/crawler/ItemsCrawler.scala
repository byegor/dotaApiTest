package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.dto.ItemDTO
import com.eb.schedule.services.ItemService
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory


class ItemsCrawler @Inject()(itemService: ItemService){

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    val steamItems: JSONArray = getItemsInfoFromSteam()
    var items: List[ItemDTO] = Nil
    for (i <- 0 until steamItems.length()) {
      val itemJson: JSONObject = steamItems.getJSONObject(i)
      items ::= new ItemDTO(itemJson.getInt("id"), parseName(itemJson.getString("name")))
    }
    itemService.insert(items)
  }

  def parseName(name: String): String = {
    name
//    name.replace("item_", "").replace("_", " ")
  }


  def getItemsInfoFromSteam(): JSONArray = {
    val teamInfo: JSONObject = HttpUtils.getResponseAsJson(GET_ITEMS)
    val result: JSONObject = teamInfo.getJSONObject("result")
    val items: JSONArray = result.getJSONArray("items")
    items
  }

}
