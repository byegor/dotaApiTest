package com.eb.schedule.model


import com.eb.schedule.model.dao._
import com.eb.schedule.model.db.MysqlDB
import com.eb.schedule.model.services._

/**
  * Created by Egor on 20.02.2016.
  */
object AppConfig {
  val teamComponent = new TeamServiceImplComponent with TeamRepositoryImplComponent with MysqlDB
  val taskComponent = new UpdateTaskServiceImplComponent with UpdateTaskRepositoryImplComponent with MysqlDB
  val leagueComponent = new LeagueServiceImplComponent with LeagueRepImpComp with MysqlDB
  val pickComponent = new PickServiceImplComponent with PickRepImplComp with MysqlDB
  val scheduledGameComponent = new ScheduledGameServiceImplComponent with ScheduledGameRepImplComp with MysqlDB
  val liveGameComponent = new LiveGameServiceImplComponent with LiveGameRepImplComp with MysqlDB

  val teamService = teamComponent.teamService
  val taskService = taskComponent.taskService
  val leagueService = leagueComponent.leagueService
  val pickService = pickComponent.pickService
  val scheduledGameService = scheduledGameComponent.scheduledGameService
  val liveGameService = liveGameComponent.liveGameService
}
