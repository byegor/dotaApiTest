package com.eb.schedule.dto

import java.sql.Timestamp

/**
  * Created by Egor on 15.03.2016.
  */
//todo mostly the same as LiveGame
case class ScheduledGameDTO(id:Int, matchId: Option[Long] = None, radiantTeam: TeamDTO, direTeam: TeamDTO, league: LeagueDTO, startDate:Timestamp, status: Byte)
