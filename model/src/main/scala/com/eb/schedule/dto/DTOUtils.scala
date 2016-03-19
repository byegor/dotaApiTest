package com.eb.schedule.dto

import com.eb.schedule.model.slick._

/**
  * Created by Egor on 13.03.2016.
  */
object DTOUtils {


  def createLiveGameDTO(g: LiveGame):LiveGameDTO={
    new LiveGameDTO(g.matchId, new TeamDTO(g.radiant, ""), new TeamDTO(g.dire, ""), new LeagueDTO(g.leagueId, ""), g.seriesType, g.startDate, g.radiantWin, g.game)
  }
  def createLiveGameWithTeamDTO(g: LiveGame, radiant: Team, dire:Team):LiveGameDTO={
    new LiveGameDTO(g.matchId, new TeamDTO(radiant.id, radiant.name, radiant.tag), new TeamDTO(dire.id, dire.name, dire.tag), new LeagueDTO(g.leagueId, ""), g.seriesType, g.startDate, g.radiantWin, g.game)
  }

  def fillLiveGameWithPicks(g:LiveGameDTO, picks: Seq[Pick]): LiveGameDTO ={
    for(p <- picks){
      if(p.radiant && p.pick){
        g.radiant.picks ::=crateDTO(p)
      }else if(p.radiant){
        g.radiant.bans ::=crateDTO(p)
      }else if(!p.radiant && p.pick){
        g.dire.picks ::= crateDTO(p)
      }else{
        g.dire.bans ::= crateDTO(p)
      }
    }
    g
  }

  def createUpdateTaskDTO(updateTask: UpdateTask): TaskDTO = {
    new TaskDTO(updateTask.id, updateTask.classname, updateTask.result)
  }

  def crateDTO(g:ScheduledGame):ScheduledGameDTO = {
    new ScheduledGameDTO(g.id, g.matchId, new TeamDTO(g.radiant, ""), new TeamDTO(g.dire, ""), new LeagueDTO(g.leagueId), g.startDate, g.status, g.radiantScore, g.direScore)
  }

  def crateDTO(l:League):LeagueDTO = {
    new LeagueDTO(l.id, l.name)
  }

  def crateDTO(p:Pick):PickDTO = {
    new PickDTO(p.heroId)
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

  def transformScheduledGameFromDTO(g:ScheduledGameDTO):ScheduledGame = {
    new ScheduledGame(g.id, g.matchId, g.radiantTeam.id, g.direTeam.id, g.league.id, g.startDate, g.status, g.radiantScore, g.direScore)
  }

  def transformPickFromDTO(matchId: Long, t: TeamDTO, isRadiant: Boolean): List[Pick] = {
    t.picks.map(p => new Pick(matchId, p.heroId, isRadiant, true)) :::
      t.bans.map(p => new Pick(matchId, p.heroId, isRadiant, false))
  }
}
