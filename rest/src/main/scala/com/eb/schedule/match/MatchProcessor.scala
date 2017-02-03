package com.eb.schedule

import com.eb.schedule.utils.HttpUtils
import com.google.gson.JsonObject
import com.google.inject.Inject
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 23.03.2016.
  */
class MatchProcessor @Inject()(matchParser: MatchParser, val httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=D998B8BDFA96FAA893E52903D6A77EEA&match_id="

  def findMatch(matchId: Long): Option[MatchDTO] = {
    try {
      val matchDetails: Option[MatchDTO] = getMatchDetails(matchId)
      matchDetails
    } catch {
      case e: Throwable => log.error("Error on getting matchId " + matchId, e)
        None
    }
  }

  def getMatchDetails(matchId: Long): Option[MatchDTO] = {
    val response: JsonObject = httpUtils.getResponseAsJson(GET_MATCH_DETAILS + matchId)
    val result: JsonObject = response.getAsJsonObject("result")
    if (result.has("error")) {
      None
    } else {
      Some(matchParser.parseMatch(result))
    }
  }


}
