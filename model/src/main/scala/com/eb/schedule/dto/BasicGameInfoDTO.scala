package com.eb.schedule.dto

import java.sql.Timestamp

import com.eb.schedule.model.SeriesType

/**
  * Created by Egor on 15.03.2016.
  */
case class BasicGameInfoDTO(matchId: Long){
  var radiantTeam: TeamDTO = ???
  var direTeam: TeamDTO = ???
  var league: LeagueDTO = ???
  var duration:Double = 0
  var radiantScore: Byte = 0
  var direScore: Byte = 0
  var seriesType:SeriesType = ???
  var radiantWin:Byte = 0
  var direWin:Byte = 0
}
