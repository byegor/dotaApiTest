package com.eb.schedule.model.services

import com.eb.schedule.model.{BasicTest, TestConfig}
import com.eb.schedule.model.dao.TeamRepositoryImplComponent
import com.eb.schedule.model.db.H2DB
import slick.driver.H2Driver.api._
import com.eb.schedule.model.slick.{Teams, Team}
import org.scalatest.FunSuite
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 20.02.2016.
  */
class TeamServiceTest extends BasicTest {

  test("first") {

    val teamService: (TeamServiceImplComponent with TeamRepositoryImplComponent with H2DB with Object)#TeamServiceImpl = TestConfig.teamService
    Await.result(teamService.insert(new Team(3, "someName", "someTag")), Duration.Inf)
    val result: Boolean = Await.result(teamService.exists(3), Duration.Inf)
    assert(result)
  }

}
