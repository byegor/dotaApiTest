package com.eb.schedule.utils

import com.eb.schedule.dto.{NetWorthDTO, _}
import com.eb.schedule.model.slick._
import com.eb.schedule.model.{MatchStatus, SeriesType}

/**
  * Created by Egor on 13.03.2016.
  */
object DTOUtils {

  def createUpdateTaskDTO(updateTask: UpdateTask): TaskDTO = {
    new TaskDTO(updateTask.id, updateTask.classname, updateTask.result)
  }

  def crateDTO(g: ScheduledGame): ScheduledGameDTO = {
    new ScheduledGameDTO(g.id, new TeamDTO(g.radiant), new TeamDTO(g.dire), new LeagueDTO(g.leagueId), SeriesType.fromCode(g.seriesType), g.startDate, MatchStatus.fromValue(g.status))
  }

  def crateDTO(l: League): LeagueDTO = {
    new LeagueDTO(l.id, l.name)
  }

  def crateDTO(l: MatchSeries): SeriesDTO = {
    new SeriesDTO(l.scheduledGameId, l.matchId, l.gameNumber, l.radiantWin, l.finished, l.radiantTeam, l.startDate)
  }

  def crateMatchDTO(series: Option[MatchSeries]): Option[SeriesDTO] = {
    series match {
      case Some(l) => Some(new SeriesDTO(l.scheduledGameId, l.matchId, l.gameNumber, l.radiantWin, l.finished, l.radiantTeam, l.startDate))
      case None => None
    }

  }

  def crateNetWorthDTO(nw: Option[NetWorth]): Option[NetWorthDTO] = {
    val res: Option[NetWorthDTO] = nw match {
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

  def crateTeamDTO(t: Option[Team]): Option[TeamDTO] = {
    if (t.isDefined) {
      val team: Team = t.get
      val dto: TeamDTO = new TeamDTO(team.id)
      dto.name = team.name
      dto.tag = team.tag
      dto.logo = team.logo
      Some(dto)
    } else {
      None
    }
  }

  def transformMatchSeriesFromDTO(s: SeriesDTO): MatchSeries = {
    new MatchSeries(s.gameId, s.matchId, s.gameNumber, s.radiantWin, s.finished, s.radiantTeamId, s.startDate)
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
    new ScheduledGame(g.id, g.radiantTeam.id, g.direTeam.id, g.league.id, g.seriesType.code, g.startDate, g.matchStatus.status)
  }


}
