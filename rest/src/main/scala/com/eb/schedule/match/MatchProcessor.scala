package com.eb.schedule

.match

import java.sql.Timestamp

import com.eb.schedule.dto._
import com.eb.schedule.live.GameContainer
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.{ScheduledGameService, UpdateTaskService}
import com.eb.schedule.model.slick.{MatchSeries, UpdateTask}
import com.eb.schedule.services.{NetWorthService, SeriesService}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 23.03.2016.
  */
//todo log to file
class MatchProcessor @Inject()(matchParser: MatchParser, val httpUtils: HttpUtils) {

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&match_id="

  val finished: mutable.HashSet[Long] = new mutable.HashSet[Long]()

  def findMatch(matchId: Long): Unit = {
    try {
      val matchDetails: Option[MatchDTO] = getMatchDetails(matchId)
      if (matchDetails.isDefined) {
        GameContainer.putMatch(matchDetails.get)
      }
    } catch {
      case e: Throwable => log.error("error", e)
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
