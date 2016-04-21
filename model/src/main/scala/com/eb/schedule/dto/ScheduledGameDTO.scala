package com.eb.schedule.dto

import java.sql.Timestamp

import com.eb.schedule.model.MatchStatus

/**
  * Created by Egor on 15.03.2016.
  */
case class ScheduledGameDTO(id:Int, radiantTeam: TeamDTO, direTeam: TeamDTO, league: LeagueDTO, startDate:Timestamp, var matchStatus: MatchStatus)
