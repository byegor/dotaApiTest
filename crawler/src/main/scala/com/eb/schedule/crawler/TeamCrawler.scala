package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.model.Failed
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class TeamCrawler @Inject() (teamService: TeamService, taskService: UpdateTaskService) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    val tasks: Future[Seq[UpdateTask]] = taskService.getPendingTasks(Team.getClass.getSimpleName)
    val result: Seq[UpdateTask] = Await.result(tasks, Duration.Inf)
    result.foreach(task => storeTeam(task.id.toInt))
  }

  private def storeTeam(teamId: Int): Unit = {
    val info: Option[Team] = getTeamInfo(teamId)
    if (info.isDefined) {
      teamService.saveOrUpdateTeamAndTask(info.get)
    } else {
      taskService.update(new UpdateTask(teamId, Team.getClass.getSimpleName, Failed.status.toByte))
    }
  }

  private def getTeamInfo(teamId: Int): Option[Team] = {
    try {
      val team: JSONObject = getTeamInfoFromSteam(teamId)
      val id: Int = team.getInt("team_id")
      if (id == teamId) {
        val logoUid: Long = team.getLong("logo")
        val name: String = team.getString("name")
        val tag: String = team.getString("tag")
        downloadTeamLogo(logoUid, tag)
        Some(new Team(teamId, name, tag))
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

  def getTeamInfoFromSteam(teamId: Int): JSONObject = {
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
