package org.egor.dota.utils

import com.mashape.unirest.http.{HttpResponse, JsonNode, Unirest}
import org.json.JSONObject

/**
 * Created by Егор on 13.07.2015.
 */
object HttpUtils {
  private val HEROES: String = "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"
  val GET_BATCH_OF_MATCHES_BASED_ON_USER: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?min_players=10&key=9EBD51CD27F27324F1554C53BEDA17C3&account_id=%s"
  val GET_NEXT_PAGE_OF_MATCHES: String = GET_BATCH_OF_MATCHES_BASED_ON_USER + "&start_at_match_id=%s"
  val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&match_id="
  val GET_BATCH_OF_MATCHES_BY_SEQUENCE: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistoryBySequenceNum/v0001/?min_players=10&key=9EBD51CD27F27324F1554C53BEDA17C3&start_at_match_seq_num="

  val lock: AnyRef = new Object()

  def getMatchHistoryResults(userId:Int, lastMatchId: Long): JSONObject = {
    val requestUrl = if (lastMatchId == -1) GET_BATCH_OF_MATCHES_BASED_ON_USER.format(userId) else GET_NEXT_PAGE_OF_MATCHES.format(userId, lastMatchId)
    getResponseAsJson(requestUrl)
  }

  def getMatchDetailsResults(matchId: Long): JSONObject = {
    getResponseAsJson(GET_MATCH_DETAILS + matchId)
  }

  def getMatchDetailsResultsBySequence(seqNum: Long): JSONObject = {
    getResponseAsJson(GET_BATCH_OF_MATCHES_BY_SEQUENCE + seqNum)
  }

  private def getResponseAsJson(url: String): JSONObject = {
    lock.synchronized {
      for (i <- 0 to 10) {
        try {
          val response: HttpResponse[JsonNode] = Unirest.get(url).asJson()
          return response.getBody.getObject
        } catch {
          case e: Exception => if (i == 9) throw e else Thread.sleep(2000)
        }
      }
      new JSONObject()
    }
  }

}
