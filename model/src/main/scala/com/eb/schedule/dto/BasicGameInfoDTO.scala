package com.eb.schedule.dto

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

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
    val minutes: Long = TimeUnit.SECONDS.toMinutes(duration.toLong)
    minutes + ":" + (duration % minutes)
  }

  def getMatchScore() = {
    radiantScore + ":" + direScore
  }
}
