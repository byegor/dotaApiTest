package com.eb.schedule

import com.eb.schedule.utils.HttpUtils
import org.json.JSONObject

import scala.io.BufferedSource

/**
  * Created by Egor on 11.04.2016.
  */
class HttpUtilsMock extends HttpUtils {

  override def getResponseAsJson(url: String): JSONObject = {
    val source: BufferedSource = io.Source.fromURL(getClass.getResource("/live.json"))
    val lines = try source.mkString finally source.close()
    val liveMatchResult: JSONObject = new JSONObject(lines)
    liveMatchResult
  }
}
