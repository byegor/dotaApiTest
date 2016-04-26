package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.dto.{TeamDTO, TaskDTO}
import com.eb.schedule.model.Failed
import com.eb.schedule.model.services.{TeamService, UpdateTaskService}
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}


class TeamCrawlerRunner @Inject()(teamService: TeamService, taskService: UpdateTaskService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    try {
      val tasks: Future[Seq[TaskDTO]] = taskService.getPendingTeamTasks()
      val result: Seq[TaskDTO] = Await.result(tasks, 10.seconds)
      result.foreach(task => storeTeam(task.id.toInt))
    }catch {
      case e : Throwable => println(e.getMessage)
    }
  }

  private def storeTeam(teamId: Int): Unit = {
    val info: Option[TeamDTO] = getTeamInfo(teamId)
    if (info.isDefined) {
      teamService.saveOrUpdateTeamAndTask(info.get)
    } else {
      taskService.update(new UpdateTask(teamId, Team.getClass.getSimpleName, Failed.status.toByte))
    }
  }

  private def getTeamInfo(teamId: Int): Option[TeamDTO] = {
    try {
      val team: JSONObject = getTeamInfoFromSteam(teamId)
      val id: Int = team.getInt("team_id")
      if (id == teamId) {
        val teamDto: TeamDTO = new TeamDTO(teamId)
        teamDto.name = team.getString("name")
        teamDto.tag = team.getString("tag")
        teamDto.logo = team.getLong("logo")
        Some(teamDto)
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
    val teamInfo: JSONObject = httpUtils.getResponseAsJson(GET_TEAM_BY_ID + teamId)
    val result: JSONObject = teamInfo.getJSONObject("result")
    val teams: JSONArray = result.getJSONArray("teams")
    teams.getJSONObject(0)
  }

  private def getTeamLogoInfoFromSteam(logoUid: Long): JSONObject = {
    httpUtils.getResponseAsJson(GET_TEAM_LOGO + logoUid)
  }

  def downloadTeamLogo(logoUid: Long, tag: String): Unit = {
    if (logoUid > 0) {
      val logoInfo: JSONObject = getTeamLogoInfoFromSteam(logoUid)
      val data: JSONObject = logoInfo.getJSONObject("data")
      val logoUrl: String = data.getString("url")
      httpUtils.downloadFile(logoUrl, "assets/" + tag + ".png")
    }
  }
}
