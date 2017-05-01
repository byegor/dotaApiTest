package com.eb.pulse.crawler.cache

import java.util.concurrent.TimeUnit

import com.eb.schedule.utils.HttpUtils
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 26.03.2016.
  */

class PlayerCache (httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)
  private val URL = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=D998B8BDFA96FAA893E52903D6A77EEA&steamids="
  private val ADDER: Long = 76561197960265728l

  val unknownUser: String = "Unknown"


  val cache: LoadingCache[Int, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(24, TimeUnit.HOURS)
    .maximumSize(2000)
    .build(new CacheLoader[Int, String]() {
      def load(id: Int): String = {
        val accountId: Long = ADDER + id
        val json: JsonObject = httpUtils.getResponseAsJson(URL + accountId)
        val response: JsonObject = json.get("response").getAsJsonObject
        if (response.has("players")) {
          val jsonArray: JsonArray = response.get("players").getAsJsonArray
          val player: JsonObject = jsonArray.get(0).getAsJsonObject
          player.get("personaname").getAsString
        } else {
          throw new CacheItemNotFoundException
        }
      }
    })

  def getPlayerName(id: Int): String = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        log.error("couldn't find a user name: " + id, e)
        unknownUser
    }
  }

  def put(id: Int, name: String): Unit = {
    cache.put(id, name)
  }


}
