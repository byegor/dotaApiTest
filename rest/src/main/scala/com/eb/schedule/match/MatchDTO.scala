package com.eb.schedule

import com.eb.schedule.dto.{LeagueDTO, NetWorthDTO, TeamDTO}
import com.eb.schedule.shared.bean.{HeroBean, Match}

import scala.collection.JavaConversions._

/**
  * Created by Egor on 02.05.2016.
  */
class MatchDTO {
  var matchId: Long = _
  var startTime: Long = _
  var duration: Int = 0
  var radiantWin: Boolean = _

  var radiantTeam: TeamDTO = _
  var direTeam: TeamDTO = _
  var league: LeagueDTO = _

  var radiantScore: Byte = 0
  var direScore: Byte = 0
  var netWorth: NetWorthDTO = _

  var gameNumber: Byte = _
  var winByRadiant: Byte = 0
  var winByDire: Byte = 0

  def toMatch(): Match = {
    val matchBean = new Match()
    matchBean.setMatchId(matchId)
    matchBean.setStartTime(startTime)
    matchBean.setDuration(getDuration())
    matchBean.setMatchStatus(if (radiantWin) 1 else 2)
    matchBean.setRadiantTeam(radiantTeam.toTeamBean)
    matchBean.setDireTeam(direTeam.toTeamBean)
    matchBean.setMatchScore(radiantScore + " - " + direScore)

    if (netWorth != null) {
      matchBean.setNetworth(scala.collection.JavaConversions.seqAsJavaList(netWorth.netWorth.map(new java.lang.Integer(_))))
    }
    matchBean.setGameNumber(gameNumber)
    matchBean.setRadianBans(radiantTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean.setRadianPicks(radiantTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDirePicks(direTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDireBans(direTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean
  }

  def getDuration() = {
    val minutes: Int = duration / 60
    val seconds: Int = duration - minutes * 60
    minutes + ":" + "%02d".format(seconds)
  }

}
