package com.eb.schedule.dto

import java.sql.Timestamp

import com.eb.schedule.model.{MatchStatus, SeriesType}

/**
  * Created by Egor on 15.03.2016.
  */
case class ScheduledGameDTO(id: Int, radiantTeam: TeamDTO, direTeam: TeamDTO, league: LeagueDTO, seriesType: SeriesType, startDate: Timestamp, var matchStatus: MatchStatus) {

  def canEqual(other: Any): Boolean = other.isInstanceOf[ScheduledGameDTO]

  override def equals(other: Any): Boolean = other match {
    case that: ScheduledGameDTO =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    id.hashCode()
  }
}
