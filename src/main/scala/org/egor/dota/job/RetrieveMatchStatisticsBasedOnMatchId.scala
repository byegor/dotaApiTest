package org.egor.dota.job

import org.egor.dota.entity.{MatchDetails, Hero, MatchBasicInfo, MatchResult}
import org.egor.dota.utils.{DBUtils, HttpUtils, TeamUtils}
import org.json.{JSONArray, JSONObject}

/**
 * Created by Egor on 12.07.2015.
 */
class RetrieveMatchStatisticsBasedOnMatchId extends Runnable {

  override def run(): Unit = {
    val heroes: List[Hero] = DBUtils.getHeroes
    val heroById: Map[Int, Hero] = heroes.map(h => h.id -> h).toMap

    while (true) {
      val unprocessedMatchId: Option[Long] = DBUtils.getUnprocessedMatchId()
      if (unprocessedMatchId.isDefined) {
        val matchDetailsJson: JSONObject = HttpUtils.getMatchDetailsResults(unprocessedMatchId.get)
        val matchDetails: Option[MatchResult] = parseMatchDetails(matchDetailsJson, heroById)
        if (matchDetails.isDefined) {
          DBUtils.matchProcessed(matchDetails.get)
        }
      }

    }
  }

  private def parseMatchDetails(matchDetailsJson: JSONObject, heros: Map[Int, Hero]): Option[MatchResult] = {
    if (!matchDetailsJson.has("result")) {
      println("Result is empty.... Retry")
      None
    } else {
      val matchDetails: JSONObject = matchDetailsJson.getJSONObject("result")

      val id: Long = matchDetails.getLong("match_id")
      try {
        val players: JSONArray = matchDetails.getJSONArray("players")
        val duration: Int = matchDetails.getInt("duration")
        val radiantWin: Boolean = matchDetails.getBoolean("radiant_win")
        var radiantTeam: List[Hero] = Nil
        var direTeam: List[Hero] = Nil
        for (j <- 0 until players.length()) {
          val player: JSONObject = players.getJSONObject(j)
          val heroId: Int = player.getInt("hero_id")
          val slot: Int = player.getInt("player_slot")
          val hero: Option[Hero] = heros.get(heroId)
          if (hero.isDefined) {
            if (slot <= 4) radiantTeam ::= hero.get else direTeam ::= hero.get
          }
        }

        if (radiantTeam.toSet.size == 5 && direTeam.toSet.size == 5 && duration > 1200) {
          Some(new MatchResult(id, radiantWin, TeamUtils.getOrCreateIdForEachTeam(radiantTeam), TeamUtils.getOrCreateIdForEachTeam(direTeam)))
        } else {
          DBUtils.matchIdProcessed(id)
          None
        }
      } catch {
        case e: Exception => {
          println("Exception: " + e.getMessage)
          try {
            DBUtils.matchIdProcessed(id)
          } catch {
            case e: Exception => {
              DBUtils.deleteUnprocessedMatch(id)
            }
          }
          None
        }
      }
    }

  }
}
