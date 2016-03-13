package com.eb.schedule.dto

import com.eb.schedule.model.slick._

/**
  * Created by Egor on 13.03.2016.
  */
object DTOUtils {


  def createLiveGameDTO(g: LiveGame):LiveGameDTO={
    //todo other properites
    new LiveGameDTO(g.matchId, new TeamDTO(g.radiant, ""), new TeamDTO(g.dire, ""), new LeagueDTO(g.leagueId, ""), g.seriesType, g.startDate, g.radiantWin, g.game)
  }

  def createUpdateTaskDTO(updateTask: UpdateTask): TaskDTO = {
    new TaskDTO(updateTask.id, updateTask.classname, updateTask.result)
  }


  def crateDTO(l:League):LeagueDTO = {
    new LeagueDTO(l.id, l.name)
  }

  def transformLiveGameFromDTO(g: LiveGameDTO): LiveGame = {
    new LiveGame(g.matchId, g.radiant.id, g.dire.id, g.leagueDTO.id, g.seriesType, g.startDate, g.radiantWin, g.gameNumber)
  }

  def transformTeamFromDTO(t: TeamDTO): Team = {
    new Team(t.id, t.name, t.tag)
  }

  def transformLeagueFromDTO(l: LeagueDTO): League = {
    new League(l.id, l.name)
  }


  def transformPickFromDTO(matchId: Long, t: TeamDTO, isRadiant: Boolean): List[Pick] = {
    t.picks.map(p => new Pick(matchId, p.heroId, isRadiant, true)) :::
      t.bans.map(p => new Pick(matchId, p.heroId, isRadiant, false))
  }
}
