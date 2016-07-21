package com.eb.schedule

import com.eb.schedule.dto.{LeagueDTO, NetWorthDTO, TeamDTO}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.shared.bean.{HeroBean, LeagueBean, Match}
import scala.collection.JavaConversions._

/**
  * Created by Egor on 02.05.2016.
  */
class MatchDTO {
  var matchId: Long = _
  var startTime: Long = _
  var seriesType: SeriesType = _
  var duration: Double = 0
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
    matchBean.setSeriesType(seriesType.name())
    matchBean.setDuration(duration)
    matchBean.setRadiantWin(radiantWin)
    matchBean.setRadiantTeam(radiantTeam.toTeamBean())
    matchBean.setDireTeam(direTeam.toTeamBean())
    matchBean.setLeague(new LeagueBean(league.leagueId, league.name))
    matchBean.setRadiantScore(radiantScore)
    matchBean.setDireScore(direScore)
    matchBean.setNetworth(scala.collection.JavaConversions.seqAsJavaList(netWorth.netWorth.map(new java.lang.Double(_))))
    matchBean.setGameNumber(gameNumber)
    matchBean.setRadianBans(radiantTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean.setRadianPicks(radiantTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDirePicks(direTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDireBans(direTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean
  }
}
