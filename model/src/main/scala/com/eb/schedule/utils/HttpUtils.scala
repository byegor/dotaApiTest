package com.eb.schedule.utils

import java.io._
import java.net.URL

import com.fasterxml.jackson.databind
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.mashape.unirest.http.{HttpResponse, Unirest}
import org.json.JSONObject


object HttpUtils {

  private val mapper: ObjectMapper = new ObjectMapper()
  private val lock: AnyRef = new Object()

  //todo
  def getResponseAsJson(url: String): JSONObject = {
    lock.synchronized {
      for (i <- 0 to 10) {
        try {
          mapper.disable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
          val response: HttpResponse[String] = Unirest.get(url).asString()
          val tree: databind.JsonNode = mapper.readTree(response.getBody.getBytes)

          return new JSONObject(tree.toString)
        } catch {
          case e: Exception => if (i == 9) throw e else Thread.sleep(1500)
        }
      }
      new JSONObject()
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
    val response = out.toByteArray()
    val fos = new FileOutputStream(fileName);
    fos.write(response)
    fos.close()
  }

}
