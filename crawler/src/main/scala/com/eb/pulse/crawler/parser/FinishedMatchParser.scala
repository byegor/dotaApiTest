package com.eb.pulse.crawler.parser

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.model.{FinishedMatch, Player, TeamScoreBoard}
import com.eb.schedule.model.slick.Team
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 16.04.2017.
  */
class FinishedMatchParser extends Lookup{

  private val log = LoggerFactory.getLogger(this.getClass)


  def parseMatch(jsonObject: JsonObject): Option[FinishedMatch] = {
    try {
      val matchId = jsonObject.get("match_id").getAsLong
      val duration = jsonObject.get("duration").getAsInt
      val radiantWin = jsonObject.get("radiant_win").getAsBoolean
      val startTime = jsonObject.get("start_time").getAsLong
      val radiantScore = jsonObject.get("radiant_score").getAsByte
      val direScore = jsonObject.get("dire_score").getAsByte
      val league = jsonObject.get("leagueid").getAsInt
      val direBarrackStatus = jsonObject.get("barracks_status_dire").getAsInt

      val direTowerStatus = jsonObject.get("tower_status_dire").getAsInt
      val radiantBarrackStatus = jsonObject.get("barracks_status_radiant").getAsInt
      val radiantTowerStatus = jsonObject.get("tower_status_radiant").getAsInt
      val radiantTeam = getTeam(jsonObject, "radiant_team_id", "radiant_name", "radiant_logo")

      val direTeam = getTeam(jsonObject, "dire_team_id", "dire_name", "dire_logo")
      val playersList: JsonArray = jsonObject.get("players").getAsJsonArray

      val players = getPlayers(playersList)

      val picks = getMatchPicks(jsonObject)

      val radiantScoreBoard = TeamScoreBoard(radiantTeam, players._1, picks.filter(p => p.radiant && p.pick).map(_.heroId), picks.filter(p => p.radiant && !p.pick).map(_.heroId),
        radiantScore, radiantTowerStatus, radiantBarrackStatus)

      val direScoreBoard = TeamScoreBoard(direTeam, players._2, picks.filter(p => !p.radiant && p.pick).map(_.heroId), picks.filter(p => !p.radiant && !p.pick).map(_.heroId),
        direScore, direTowerStatus, direBarrackStatus)

      val networth = netWorthService.findByMatchId(matchId)

      Some(FinishedMatch(matchId, startTime, duration, radiantWin, radiantScoreBoard, direScoreBoard, league, networth, radiantScore, direScore))
    } catch {
      case e: Throwable => log.error("Couldn't parse finished match: " + jsonObject, e)
        None
    }
  }


  private def getPlayers(playersList: JsonArray): (List[Player], List[Player]) = {
    var radiantPlayers, direPlayers = Nil
    for (i <- 0 until playersList.size()) {
      val player: JsonObject = playersList.get(i).getAsJsonObject
      val accountId: Int = player.get("account_id").getAsInt
      val heroId: Int = player.get("hero_id").getAsInt
      val kills = player.get("kills").getAsInt
      val deaths = player.get("deaths").getAsInt
      val assists = player.get("assists").getAsInt
      val level = player.get("level").getAsInt
      var items = Nil
      for (i <- 0 until 6) {
        val itemId: Int = player.get("item_" + i).getAsInt
        items ::= itemId
      }
      val p = Player(accountId, "", heroId, items, level, kills, deaths, assists, 0)
      val isRadiant = player.get("player_slot").getAsInt < 5
      if (isRadiant) radiantPlayers ::= p else direPlayers ::= p
    }
    (radiantPlayers, direPlayers)
  }

  def getTeam(json: JsonObject, idTag: String, nameTag: String, logoTag: String): Team = {
    try {
      val teamId: Int = json.get(idTag).getAsInt
      val name = json.get(nameTag).getAsString
      val logo = json.get(logoTag).getAsLong

      Team(teamId, name, "", logo)
    } catch {
      case e: Throwable => log.error("Couldn't parse team", e)
        new Team(-1)
    }
  }

  def getMatchPicks(json: JsonObject): List[Pick] = {
    var picks = Nil
    if (json.has("picks_bans")) {
      val pickAndBans: JsonArray = json.get("picks_bans").getAsJsonArray
      for (i <- 0 until pickAndBans.size()) {
        val pick: JsonObject = pickAndBans.get(i).getAsJsonObject
        val heroId: Int = pick.get("hero_id").getAsInt
        val isRadiant: Boolean = pick.get("team").getAsInt == 0
        val isPick: Boolean = pick.get("is_pick").getAsBoolean
        picks ::= Pick(heroId, isRadiant, isPick)
      }
    }
    picks
  }


}

case class Pick(heroId: Int, radiant: Boolean, pick: Boolean)