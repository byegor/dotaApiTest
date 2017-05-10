package com.eb.schedule.utils

import java.io._
import java.net.URL

import com.google.gson.{JsonObject, JsonParser}
import com.mashape.unirest.http.{HttpResponse, Unirest}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory


class HttpUtils {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val lock: AnyRef = new Object()
  private val jsonParser = new JsonParser

  val config: Config = ConfigFactory.load()
  val REST_API_URL: String = config.getString("restapi.url") + "/v1/"

  def getResponseAsJson(url: String): JsonObject = {
    lock.synchronized {
      for (i <- 0 to 10) {
        try {
          val response: HttpResponse[String] = Unirest.get(url).asString()
          val result: JsonObject = jsonParser.parse(response.getBody).getAsJsonObject
          return result
        } catch {
          case e: Exception => if (i == 9) throw e else Thread.sleep(1500)
        }
      }
      new JsonObject()
    }
  }

  def downloadFile(logoUrl: String, fileName: String) {
    val url: URL = new URL(logoUrl)
    val in = new BufferedInputStream(url.openStream())
    val out = new ByteArrayOutputStream()
    val buf: Array[Byte] = Array.ofDim(1024)
    var n = in.read(buf)
    while (n != -1) {
      out.write(buf, 0, n)
      n = in.read(buf)
    }
    out.close()
    in.close()
    val response = out.toByteArray
    val fos = new FileOutputStream(fileName)
    fos.write(response)
    fos.close()
  }

  def sendData(data: String) {
    try {
      val result = Unirest.put(REST_API_URL).header("content-type", "application/json").body(data).asString()
    } catch {
      case e: Throwable => log.error("Couldn't send data to rest api:  " + REST_API_URL, e)
    }

  }

}
