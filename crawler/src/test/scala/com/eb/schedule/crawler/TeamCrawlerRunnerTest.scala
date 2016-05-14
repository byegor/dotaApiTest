package com.eb.schedule.crawler

import com.eb.schedule.dto.TeamDTO
import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.slick.{Team, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject, JsonParser}
import jdk.nashorn.internal.parser.JSONParser
import org.json.{JSONArray, JSONObject}
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */

class TeamCrawlerRunnerTest extends BasicTest {

  test("crawle and create team") {
    taskService.insert(new UpdateTask(36l, Team.getClass.getSimpleName, 0.toByte))

    val crawler = org.mockito.Mockito.spy(new TeamCrawlerRunner(teamService, taskService, new HttpUtils() {
      override def getResponseAsJson(url: String): JsonObject = {
        teamJsonResponse.getAsJsonObject
      }
    }))
    doNothing().when(crawler).downloadTeamLogo(anyObject(), anyObject())
    crawler.run()
    Thread.sleep(1000)
    val taks: UpdateTask = Await.result(taskService.findByIdAndName(36l, Team.getClass.getSimpleName), Duration.Inf)
    assert(1.toByte == taks.result)
    val teamOpt: Option[TeamDTO] = Await.result(teamService.findById(36), Duration.Inf)
    assert(teamOpt.isDefined)
    val team = teamOpt.get
    val jsonTeam: JsonObject = getJsonTeam
    assert(jsonTeam.get("name").getAsString == team.name)
    assert(jsonTeam.get("tag").getAsString == team.tag)
    assert(jsonTeam.get("team_id").getAsInt == team.id)
  }

  test("crawle and update team") {
    taskService.insert(new UpdateTask(36l, Team.getClass.getSimpleName, 0.toByte))
    val teamDTO: TeamDTO = new TeamDTO(36)
    teamService.insert(teamDTO)

    val crawler = org.mockito.Mockito.spy(new TeamCrawlerRunner(teamService, taskService, new HttpUtils() {
      override def getResponseAsJson(url: String): JsonObject = {
        teamJsonResponse.getAsJsonObject
      }
    }))
    when(crawler.getTeamInfoFromSteam(36)).thenReturn(getJsonTeam)
    doNothing().when(crawler).downloadTeamLogo(anyObject(), anyObject())
    crawler.run()
    Thread.sleep(1000)
    taskService.findByIdAndName(36l, Team.getClass.getSimpleName).onSuccess {
      case taks => {
        assert(1.toByte == taks.result)
        val teamOpt: Option[TeamDTO] = Await.result(teamService.findById(36), Duration.Inf)
        assert(teamOpt.isDefined)
        val team = teamOpt.get
        val jsonTeam: JsonObject = getJsonTeam
        assert(jsonTeam.get("name").getAsString == team.name)
        assert(jsonTeam.get("tag").getAsString == team.tag)
        assert(jsonTeam.get("team_id").getAsInt == team.id)
        assert(jsonTeam.get("logo").getAsLong == team.logo)
      }
    }
  }

  def getJsonTeam: JsonObject = {
    val result: JsonObject = teamJsonResponse.getAsJsonObject.get("result").getAsJsonObject
    val teams: JsonArray = result.get("teams").getAsJsonArray
    teams.get(0).getAsJsonObject
  }

  val teamJsonResponse = new JsonParser().parse("{\n\"result\": {\n\"status\": 1,\n\"teams\": [\n{\n\"team_id\": 36,\n\"name\": \"Natus Vincere\",\n\"tag\": \"Na`Vi\",\n\"time_created\": 1338843412,\n\"rating\": \"inactive\",\n\"logo\": 46499322609643214,\n\"logo_sponsor\": 0,\n\"country_code\": \"ua\",\n\"url\": \"\",\n\"games_played_with_current_roster\": 0,\n\"player_0_account_id\": 70388657,\n\"player_1_account_id\": 86723143,\n\"player_2_account_id\": 89550641,\n\"player_3_account_id\": 89625472,\n\"player_4_account_id\": 117421467,\n\"player_5_account_id\": 169181898,\n\"player_6_account_id\": 176184718,\n\"admin_account_id\": 70388657,\n\"league_id_0\": 4\n}\n]\n}\n}")

}
