package egor.dota.harvester.job

import egor.dota.harvester.utils.{DBUtils, HttpUtils}
import org.json.{JSONArray, JSONException, JSONObject}

/**
 * Created by Egor on 12.07.2015.
 */
class RetrieveUserAndMatchBasedOnUser extends Runnable {

  override def run(): Unit = {
    while (true) {
      try {
        val userOpt: Option[Int] = DBUtils.getUserForProcess()
        if (userOpt.isDefined) {
          var lastMatchProcessed: Long = -1
          var reachEnd: Boolean = false
          val userId: Int = userOpt.get
          while (!reachEnd) {
            val historyResults: JSONObject = HttpUtils.getMatchHistoryResults(userId, lastMatchProcessed)
            val matchesAndUsers: Option[(List[Long], List[Int])] = parseMatchHistory(historyResults)
            var mathces: List[Long] = matchesAndUsers.get._1
            if (lastMatchProcessed == -1) {
              lastMatchProcessed = mathces.head
            } else {
              lastMatchProcessed = mathces.head
              mathces = mathces.tail
            }
            val users: List[Int] = matchesAndUsers.get._2
            DBUtils.insertUsers(users)
            DBUtils.insertMatches(mathces)
            if (historyResults.getJSONObject("result").getInt("results_remaining") == 0) {
              reachEnd = true
            }
          }
          DBUtils.updateUserProgress(userId, lastMatchProcessed)
        }
      } catch {
        case e: JSONException => {}
      }
    }
  }

  private def parseMatchHistory(matchHistory: JSONObject): Option[(List[Long], List[Int])] = {
    var matches: List[Long] = Nil
    var users: List[Int] = Nil
    val result: JSONObject = matchHistory.getJSONObject("result")

    val matchesList: JSONArray = result.getJSONArray("matches")

    for (i <- 0 until matchesList.length()) {
      val matchJson: JSONObject = matchesList.getJSONObject(i)
      val id: Long = matchJson.getLong("match_id")
      matches ::= id

      val players: JSONArray = matchJson.getJSONArray("players")
      for (j <- 0 until players.length()) {
        val player: JSONObject = players.getJSONObject(j)
        val userId: Int = player.getInt("account_id")
        users ::= userId
      }
    }
    Some((matches, users))

  }
}
