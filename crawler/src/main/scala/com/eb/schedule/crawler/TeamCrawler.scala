package com.eb.schedule.crawler

import com.eb.schedule.model.{Failed, Finished}
import com.eb.schedule.utils.{DBUtils, HttpUtils}
import egor.dota.model.entity.TeamInfo
import org.json.{JSONArray, JSONObject}
import CrawlerUrls._
import org.slf4j.LoggerFactory


class TeamCrawler extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    val tasks: List[(Int, String)] = DBUtils.getPendingUpdateTasks(TeamInfo.getClass.getName)
    tasks.foreach(par => storeTeam(par._1))
  }

  private def storeTeam(teamId: Int): Unit = {
    val info: Option[TeamInfo] = getTeamInfo(teamId)
    if (info.isDefined) {
      DBUtils.updateOrCreateTeamInfo(info.get)
      DBUtils.updateResultInTask(teamId, TeamInfo.getClass.getName, Finished)
    } else {
      DBUtils.updateResultInTask(teamId, TeamInfo.getClass.getName, Failed)
    }
  }

  private def getTeamInfo(teamId: Int): Option[TeamInfo] = {
    try {
      val team: JSONObject = getTeamInfoFromSteam(teamId)
      val id: Int = team.getInt("team_id")
      if (id == teamId) {
        val logoUid: Long = team.getLong("logo")
        val name: String = team.getString("name")
        val tag: String = team.getString("tag")
        downloadTeamLogo(logoUid, tag)
        Some(new TeamInfo(teamId, name, tag))
      } else {
        log.error("Couldn't find such team on steam: " + teamId)
        None
      }
    } catch {
      case e: Exception => {
        log.error("Error getting team info with id: " + teamId, e)
        None
      }
    }
  }

  private def getTeamInfoFromSteam(teamId: Int): JSONObject = {
    val teamInfo: JSONObject = HttpUtils.getResponseAsJson(GET_TEAM_BY_ID + teamId)
    val result: JSONObject = teamInfo.getJSONObject("result")
    val teams: JSONArray = result.getJSONArray("teams")
    teams.getJSONObject(0)
  }

  private def getTeamLogoInfoFromSteam(logoUid: Long): JSONObject = {
    HttpUtils.getResponseAsJson(GET_TEAM_LOGO + logoUid)
  }

  def downloadTeamLogo(logoUid: Long, tag: String): Unit = {
    if (logoUid > 0) {
      val logoInfo: JSONObject = getTeamLogoInfoFromSteam(logoUid)
      val data: JSONObject = logoInfo.getJSONObject("data")
      val logoUrl: String = data.getString("url")
      HttpUtils.downloadFile(logoUrl, "assets/" + tag + ".png")
    }
  }
}
