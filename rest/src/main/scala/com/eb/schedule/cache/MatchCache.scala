package com.eb.schedule.cache

import java.util.concurrent.TimeUnit

import com.eb.schedule.exception.CacheItemNotFound
import com.eb.schedule.utils.HttpUtils
import com.eb.schedule.{MatchDTO, MatchParser}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.google.gson.JsonObject
import com.google.inject.Inject
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 26.03.2016.
  */
class MatchCache @Inject()(matchParser: MatchParser, val httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=D998B8BDFA96FAA893E52903D6A77EEA&match_id="

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  private val cache: LoadingCache[Long, String] = CacheBuilder.newBuilder()
    .expireAfterAccess(4, TimeUnit.HOURS)
    .maximumSize(100)
    .build(new CacheLoader[Long, String]() {
      def load(mathcId: Long): String = {
        findMatch(mathcId) match {
          case Some(matchDTO) => mapper.writeValueAsString(matchDTO.toMatch())
          case None => throw new CacheItemNotFound
        }
      }
    }).asInstanceOf[LoadingCache[Long, String]]

  private def findMatch(matchId: Long): Option[MatchDTO] = {
    try {
      val response: JsonObject = httpUtils.getResponseAsJson(GET_MATCH_DETAILS + matchId)
      val result: JsonObject = response.getAsJsonObject("result")
      if (result.has("error")) {
        None
      } else {
        Some(matchParser.parseMatch(result))
      }
    } catch {
      case e: Throwable => log.error("Error on getting matchId " + matchId, e)
        None
    }
  }

  def getMatch(id: Long): Option[String] = {
    try {
      Some(cache.get(id))
    } catch {
      case e: Exception =>
        if (e.getCause.isInstanceOf[CacheItemNotFound]) {

        } else {
          log.error("couldn't get match from cache: ", e)
        }
        None
    }
  }
}
