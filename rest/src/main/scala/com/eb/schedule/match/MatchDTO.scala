package com.eb.schedule

import com.eb.schedule.dto.{LeagueDTO, NetWorthDTO, TeamDTO}
import com.eb.schedule.model.SeriesType

/**
  * Created by Egor on 02.05.2016.
  */
class MatchDTO {
  var matchId: Long = _
  var startTime:Long = _
  var seriesType: SeriesType = _
  var duration: Double = 0
  var radiantWin:Boolean = _

  var radiantTeam: TeamDTO = _
  var direTeam: TeamDTO = _
  var league: LeagueDTO = _

  var radiantScore: Byte = 0
  var direScore: Byte = 0
  var netWorth: NetWorthDTO = _

  var gameNumber:Byte = _
  var winByRadiant:Byte = 0
  var winByDire:Byte = 0

}
