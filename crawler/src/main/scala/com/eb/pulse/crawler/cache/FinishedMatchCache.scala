package com.eb.pulse.crawler.cache

import java.util.concurrent.TimeUnit

import com.eb.pulse.crawler.parser.FinishedMatchParser
import com.eb.pulse.crawler.transformer.FinishedMatchToTransformer
import com.eb.schedule.shared.bean.Match
import com.eb.schedule.utils.HttpUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.gson.JsonObject
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 26.03.2016.
  */
class FinishedMatchCache(finishedMatchParser: FinishedMatchParser, val httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=D998B8BDFA96FAA893E52903D6A77EEA&match_id="

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private val cache: LoadingCache[Long, String] = CacheBuilder.newBuilder()
    .expireAfterWrite(24, TimeUnit.HOURS)
    .maximumSize(100)
    .build(new CacheLoader[Long, String]() {
      def load(mathcId: Long): String = {
        findMatch(mathcId) match {
          case Some(finishedMatch) => mapper.writeValueAsString(finishedMatch)
          case None => throw new CacheItemNotFoundException
        }
      }
    }).asInstanceOf[LoadingCache[Long, String]]

  private def findMatch(matchId: Long): Option[Match] = {
    try {
      val response: JsonObject = httpUtils.getResponseAsJson(GET_MATCH_DETAILS + matchId)
      val result: JsonObject = response.getAsJsonObject("result")
      if (result.has("error")) {
        None
      } else {
        val finishedMatch = finishedMatchParser.parseMatch(result)
        finishedMatch match {
          case Some(m) => Some(FinishedMatchToTransformer.transform(m))
          case None => None
        }
      }
    } catch {
      case e: Throwable => log.error("Error on parsing matchId " + matchId, e)
        None
    }
  }

  def getMatch(id: Long): Option[String] = {
    try {
      Some(cache.get(id))
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFoundException]) {

        } else {
          log.error("couldn't get match from cache: ", e)
        }
        None
    }
  }
}
