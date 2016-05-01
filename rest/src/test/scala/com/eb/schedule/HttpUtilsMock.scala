package com.eb.schedule

import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonObject, JsonParser}
import org.json.JSONObject

import scala.io.BufferedSource

/**
  * Created by Egor on 11.04.2016.
  */
class HttpUtilsMock extends HttpUtils {

  override def getResponseAsJson(url: String): JsonObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
    val lines = try source.mkString finally source.close()
    new JsonParser().parse(lines).getAsJsonObject
  }

  def getGame(): JsonObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/game.json"))
    val lines = try source.mkString finally source.close()
    new JsonParser().parse(lines).getAsJsonObject
  }
}
