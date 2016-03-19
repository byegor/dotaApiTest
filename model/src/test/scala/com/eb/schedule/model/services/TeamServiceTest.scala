package com.eb.schedule.model.services

import com.eb.schedule.dto.{PickDTO, TeamDTO}
import com.eb.schedule.model.BasicTest
import com.eb.schedule.model.dao.PickRepository
import com.eb.schedule.model.slick.{Pick, Team}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
class TeamServiceTest extends BasicTest {

  test("first") {
    Await.result(teamService.insert(new TeamDTO(3, "someName", "someTag")), Duration.Inf)
    val result: Boolean = Await.result(teamService.exists(3), Duration.Inf)
    assert(result)
  }

}
