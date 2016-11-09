package com.eb.schedule.dto

import com.eb.schedule.model.SeriesType

/**
  * Created by Egor on 15.03.2016.
  */
case class BasicGameInfoDTO(matchId: Long) {
  var radiantTeam: TeamDTO = null
  var direTeam: TeamDTO = null
  var league: LeagueDTO = null
  var duration: Double = 0
  var radiantScore: Byte = 0
  var direScore: Byte = 0
  var seriesType: SeriesType = null
  var radiantWin: Byte = 0
  var direWin: Byte = 0

  def getDuration() = {
    val minutes: Int = duration.toInt / 60
    val seconds: Int = duration.toInt - minutes * 60
    minutes + ":" + "%02d".format(seconds)
  }

  def getMatchScore() = {
    radiantScore + " - " + direScore
  }
}
