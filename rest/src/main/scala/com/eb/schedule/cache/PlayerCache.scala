package com.eb.schedule.cache

import java.util.concurrent.TimeUnit

import com.eb.schedule.dto.TeamDTO
import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
//todo set player name
class PlayerCache @Inject()(val teamService: TeamService, taskService: UpdateTaskService, httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)
  private val URL = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=9EBD51CD27F27324F1554C53BEDA17C3&steamids="
  private val ADDER: Long = 76561197960265728l

  val unknownUser: String = "Unknown"


  private val cache: LoadingCache[Int, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(4, TimeUnit.HOURS)
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
          throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Int, String]]

  def getPlayerName(id: Int): String = {
    try {
      cache.get(id)
    } catch {
      case e: Exception =>
        log.error("couldn't find a user name: " + id)
        unknownUser
    }
  }

  def put(id: Int, name: String): Unit = {
    cache.put(id, name)
  }


}
