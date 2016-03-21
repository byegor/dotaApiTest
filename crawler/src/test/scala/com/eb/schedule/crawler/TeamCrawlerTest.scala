package com.eb.schedule.crawler

import com.eb.schedule.dto.TeamDTO
import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.slick.{Team, UpdateTask}
import org.json.{JSONArray, JSONObject}
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Egor on 20.02.2016.
  */
class TeamCrawlerTest extends BasicTest {

  test("crawle and create team") {
    taskService.insert(new UpdateTask(36l, Team.getClass.getSimpleName, 0.toByte))

    val crawler = org.mockito.Mockito.spy(new TeamCrawler(teamService, taskService))
    when(crawler.getTeamInfoFromSteam(36)).thenReturn(getJsonTeam)
    doNothing().when(crawler).downloadTeamLogo(anyObject(), anyObject())
    crawler.run()
    Thread.sleep(1000)
    val taks: UpdateTask = Await.result(taskService.findByIdAndName(36l, Team.getClass.getSimpleName), Duration.Inf)
    assert(1.toByte == taks.result)
    val teamOpt: Option[Team] = Await.result(teamService.findById(36), Duration.Inf)
    assert(teamOpt.isDefined)
    val team = teamOpt.get
    val jsonTeam: JSONObject = getJsonTeam
    assert(jsonTeam.getString("name") == team.name)
    assert(jsonTeam.getString("tag") == team.tag)
    assert(jsonTeam.getInt("team_id") == team.id)
  }

  test("crawle and update team") {
    taskService.insert(new UpdateTask(36l, Team.getClass.getSimpleName, 0.toByte))
    teamService.insert(new TeamDTO(36, "name", -1))

    val crawler = org.mockito.Mockito.spy(new TeamCrawler(teamService, taskService))
    when(crawler.getTeamInfoFromSteam(36)).thenReturn(getJsonTeam)
    doNothing().when(crawler).downloadTeamLogo(anyObject(), anyObject())
    crawler.run()
    Thread.sleep(1000)
    taskService.findByIdAndName(36l, Team.getClass.getSimpleName).onSuccess {
      case taks => {
        assert(1.toByte == taks.result)
        val teamOpt: Option[Team] = Await.result(teamService.findById(36), Duration.Inf)
        assert(teamOpt.isDefined)
        val team = teamOpt.get
        val jsonTeam: JSONObject = getJsonTeam
        assert(jsonTeam.getString("name") == team.name)
        assert(jsonTeam.getString("tag") == team.tag)
        assert(jsonTeam.getInt("team_id") == team.id)
        assert(jsonTeam.getLong("logo") == team.logo)
      }
    }
  }

  def getJsonTeam: JSONObject = {
    val result: JSONObject = teamJsonResponse.getJSONObject("result")
    val teams: JSONArray = result.getJSONArray("teams")
    teams.getJSONObject(0)
  }

  val teamJsonResponse = new JSONObject("{\n\"result\": {\n\"status\": 1,\n\"teams\": [\n{\n\"team_id\": 36,\n\"name\": \"Natus Vincere\",\n\"tag\": \"Na`Vi\",\n\"time_created\": 1338843412,\n\"rating\": \"inactive\",\n\"logo\": 46499322609643214,\n\"logo_sponsor\": 0,\n\"country_code\": \"ua\",\n\"url\": \"\",\n\"games_played_with_current_roster\": 0,\n\"player_0_account_id\": 70388657,\n\"player_1_account_id\": 86723143,\n\"player_2_account_id\": 89550641,\n\"player_3_account_id\": 89625472,\n\"player_4_account_id\": 117421467,\n\"player_5_account_id\": 169181898,\n\"player_6_account_id\": 176184718,\n\"admin_account_id\": 70388657,\n\"league_id_0\": 4\n}\n]\n}\n}")

}
