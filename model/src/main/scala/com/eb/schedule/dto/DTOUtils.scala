package com.eb.schedule.dto

import com.eb.schedule.model.slick._

/**
  * Created by Egor on 13.03.2016.
  */
object DTOUtils {


  def createUpdateTaskDTO(updateTask: UpdateTask): TaskDTO = {
    new TaskDTO(updateTask.id, updateTask.classname, updateTask.result)
  }

  def crateDTO(g: ScheduledGame): ScheduledGameDTO = {
    new ScheduledGameDTO(g.id, g.matchId, new TeamDTO(g.radiant), new TeamDTO(g.dire), new LeagueDTO(g.leagueId), g.startDate, g.status)
  }

  def crateDTO(l: League): LeagueDTO = {
    new LeagueDTO(l.id, l.name)
  }

  def crateDTO(l: Option[League]): Option[LeagueDTO] = {
    if (l.isDefined) {
      Some(crateDTO(l.get))
    } else {
      None
    }
  }

  def transformTeamFromDTO(t: TeamDTO): Team = {
    new Team(t.id, t.name, t.tag, t.logo)
  }

  def transformLeagueFromDTO(l: LeagueDTO): League = {
    new League(l.id, l.leagueName, None, Some(l.url))
  }

  def transformScheduledGameFromDTO(g: ScheduledGameDTO): ScheduledGame = {
    new ScheduledGame(g.id, g.matchId, g.radiantTeam.id, g.direTeam.id, g.league.id, g.startDate, g.status)
  }


}
