package com.eb.schedule.live

import com.eb.schedule.RestBasicTest
import com.eb.schedule.dto.TeamDTO
import com.google.gson.{JsonObject, JsonPrimitive}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Egor on 09.01.2017.
  */
class LiveGameHelperTest extends RestBasicTest {

  test("testParseTeam") {
    teamService.insert(new TeamDTO(12)).futureValue
    val team = new JsonObject()
    team.add("team_id", new JsonPrimitive(12))
    team.add("team_name", new JsonPrimitive("newName"))
    team.add("team_logo", new JsonPrimitive(123))
    Future{liveGameHelper.parseTeam(team)}.futureValue

    whenReady(teamService.findById(12)) {
      teamOpt =>
        assert("newName" == teamOpt.get.name, "team should be updated after parsing it from live request")
        assert(123 == teamOpt.get.logo)
    }
  }

}
