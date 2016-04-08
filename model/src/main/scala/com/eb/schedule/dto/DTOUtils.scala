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

  def crateDTO(l: MatchSeries): SeriesDTO = {
    new SeriesDTO(l.scheduledGameId, l.matchId, l.gameNumber)
  }

  def crateNetWorthDTO(nw: Option[NetWorth]): Option[NetWorthDTO] = {
    val res:Option[NetWorthDTO] = nw match {
      case Some(n) => Some(new NetWorthDTO(n.matchId, n.netWorth.split(",").map(_.toInt).toList))
      case None => None
    }
    res
  }

  def crateDTO(l: Option[League]): Option[LeagueDTO] = {
    if (l.isDefined) {
      Some(crateDTO(l.get))
    } else {
      None
    }
  }

  def transformMatchSeriesFromDTO(s: SeriesDTO): MatchSeries = {
    new MatchSeries(s.gameId, s.matchId, s.gameNumber)
  }

  def transformTeamFromDTO(t: TeamDTO): Team = {
    new Team(t.id, t.name, t.tag, t.logo)
  }

  def transformNetWorthFromDTO(nw: NetWorthDTO): NetWorth = {
    new NetWorth(nw.matchId, nw.netWorth.mkString(","))
  }

  def transformLeagueFromDTO(l: LeagueDTO): League = {
    new League(l.id, l.leagueName, None, Some(l.url))
  }

  def transformScheduledGameFromDTO(g: ScheduledGameDTO): ScheduledGame = {
    new ScheduledGame(g.id, g.matchId, g.radiantTeam.id, g.direTeam.id, g.league.id, g.startDate, g.status)
  }


}
