package org.egor.dota.trash

import org.egor.dota.entity.{Hero, MatchDetails}
import org.egor.dota.utils.{DBUtils, HttpUtils, TeamUtils}
import org.json.{JSONArray, JSONObject}

/**
 * Created by Egor on 12.07.2015.
 */
class TrashRetrieveMatchStatisticsBasedOnDetailsJob extends Runnable {

  override def run(): Unit = {
   /* val heroes: List[Hero] = DBUtils.getHeroes
    val heroById: Map[Int, Hero] = heroes.map(h => h.id -> h).toMap*/

   /* while (true) {
      val unprocessedMatchId: Long = DBUtils.getUnprocessedMatchId()
      val matchDetailsJson: JSONObject = HttpUtils.getMatchDetailsResults(unprocessedMatchId)
      val matchDetails: Option[MatchDetails] = parseMatchDetails(matchDetailsJson, heroById)
      if (matchDetails.isDefined) {
        DBUtils.insertMatchDetails(matchDetails.get)
      }
    }*/
  }

  private def parseMatchDetails(matchDetailsJson: JSONObject, heros: Map[Int, Hero]): Option[MatchDetails] = {
    if (!matchDetailsJson.has("result")) {
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
        var accountIds: List[Int] = Nil
        for (j <- 0 until players.length()) {
          val player: JSONObject = players.getJSONObject(j)
          val heroId: Int = player.getInt("hero_id")
          val slot: Int = player.getInt("player_slot")
          val accountId: Int = player.getInt("account_id")
          accountIds ::= accountId
          val hero: Option[Hero] = heros.get(heroId)
          if (hero.isDefined) {
            if (slot <= 4) radiantTeam ::= hero.get else direTeam ::= hero.get
          }

        }

        if (radiantTeam.length == 5 && direTeam.length == 5 && accountIds.length == 10) {
          Some(new MatchDetails(id, radiantWin, duration, TeamUtils.getOrCreateIdForEachTeam(radiantTeam), TeamUtils.getOrCreateIdForEachTeam(direTeam), accountIds))
        } else {
          DBUtils.matchIdProcessed(id)
          None
        }
      } catch {
        case e: Exception => {
          DBUtils.matchIdProcessed(id)
          None
        }
      }
    }

  }
}
