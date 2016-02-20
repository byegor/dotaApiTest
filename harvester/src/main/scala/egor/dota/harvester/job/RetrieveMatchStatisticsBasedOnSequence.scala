package egor.dota.harvester.job

import egor.dota.harvester.utils.{DBUtils, HttpUtils, TeamUtils}
import egor.dota.model.entity.{Hero, MatchBasicInfo, MatchResult}
import org.json.{JSONArray, JSONObject}

/**
 * Created by Egor on 12.07.2015.
 */
class RetrieveMatchStatisticsBasedOnSequence(jobId: Int) extends Runnable {


  def isNormalMatch(matchResult: MatchBasicInfo): Boolean = {
    matchResult.radiant.length == 5 && matchResult.dire.length == 5 && matchResult.duration > 1200
  }

  override def run(): Unit = {
    val heroes: List[Hero] = DBUtils.getHeroes
    val heroById: Map[Int, Hero] = heroes.map(h => h.id -> h).toMap

    while (true) {
      val latRecordedSequenceNum: Long = DBUtils.getLatRecordedMatchId(jobId)
      val matchHistoryJson: JSONObject = HttpUtils.getMatchDetailsResultsBySequence(latRecordedSequenceNum)
      val matchesInfo: List[MatchBasicInfo] = parseMatchHistory(matchHistoryJson, heroById)
      val lastMatch: MatchBasicInfo = matchesInfo.head
      val matchResults: List[MatchResult] = matchesInfo.tail
        .filter(m => isNormalMatch(m))
        .map(m => new MatchResult(m.id, m.radiantWin, TeamUtils.getOrCreateIdForEachTeam(m.radiant), TeamUtils.getOrCreateIdForEachTeam(m.dire)))
      DBUtils.insertMatchResults(matchResults, lastMatch.sequence)
    }

  }

  private def parseMatchHistory(matchHistory: JSONObject, heros: Map[Int, Hero]): List[MatchBasicInfo] = {
    var matches: List[MatchBasicInfo] = Nil
    val matchesList: JSONArray = matchHistory.getJSONObject("result").getJSONArray("matches")

    for (i <- 0 until matchesList.length()) {
      val matchJson: JSONObject = matchesList.getJSONObject(i)
      val id: Long = matchJson.getLong("match_id")
      val sequence: Long = matchJson.getLong("match_seq_num")
      val duration: Int = matchJson.getInt("duration")
      val radiantWin: Boolean = matchJson.getBoolean("radiant_win")

      val players: JSONArray = matchJson.getJSONArray("players")
      var radiantTeam: List[Hero] = Nil
      var direTeam: List[Hero] = Nil
      for ( j <- 0 until players.length()) {
        val player: JSONObject = players.getJSONObject(j)
        val heroId: Int = player.getInt("hero_id")
        val slot: Int = player.getInt("player_slot")
        val hero: Option[Hero] = heros.get(heroId)
        if(hero.isDefined){
          if (slot <= 4 ) radiantTeam ::= hero.get else direTeam ::= hero.get
        }
      }
      matches ::= new MatchBasicInfo(id, sequence, radiantWin, duration, radiantTeam, direTeam)
    }
    matches
  }
}
