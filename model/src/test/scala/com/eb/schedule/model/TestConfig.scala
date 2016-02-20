package com.eb.schedule.model

import com.eb.schedule.model.dao._
import com.eb.schedule.model.db.H2DB
import com.eb.schedule.model.services._

/**
  * Created by Egor on 20.02.2016.
  */
object TestConfig {
  val teamComponent = new TeamServiceImplComponent with TeamRepositoryImplComponent with H2DB
  val taskComponent = new UpdateTaskServiceImplComponent with UpdateTaskRepositoryImplComponent with H2DB
  val leagueComponent = new LeagueServiceImplComponent with LeagueRepImpComp with H2DB
  val pickComponent = new PickServiceImplComponent with PickRepImplComp with H2DB
  val scheduledGameComponent = new ScheduledGameServiceImplComponent with ScheduledGameRepImplComp with H2DB
  val liveGameComponent = new LiveGameServiceImplComponent with LiveGameRepImplComp with H2DB

  val teamService = teamComponent.teamService
  val taskService = taskComponent.taskService
  val leagueService = leagueComponent.leagueService
  val pickService = pickComponent.pickService
  val scheduledGameService = scheduledGameComponent.scheduledGameService
  val liveGameService = liveGameComponent.liveGameService
}
