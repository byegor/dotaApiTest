package com.eb.schedule.dto

/**
  * Created by Egor on 13.03.2016.
  */
case class LiveGameDTO(val matchId: Long, val radiant: TeamDTO, val dire: TeamDTO, val leagueDTO: LeagueDTO, val seriesType: Byte, val startDate: java.sql.Timestamp, val radiantWin: Byte, val gameNumber: Byte) {
  var duration:Double = 0
}
