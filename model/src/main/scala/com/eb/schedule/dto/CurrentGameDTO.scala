package com.eb.schedule.dto

import java.lang.Double
import scala.collection.JavaConversions._
import com.eb.schedule.shared.bean.{HeroBean, LeagueBean, Match}

/**
  * Created by Egor on 13.03.2016.
  */
class CurrentGameDTO(val matchId: Long) {
  var scheduledGameId: Int = _
  var basicInfo: BasicGameInfoDTO = new BasicGameInfoDTO(matchId)
  var direTeam: TeamDTO = _
  var radiantTeam: TeamDTO = _

  var netWorth: NetWorthDTO = _

  def toMatch(): Match = {
    val matchBean = new Match
    matchBean.setMatchId(basicInfo.matchId)
    matchBean.setDuration(basicInfo.getDuration())
    matchBean.setMatchStatus(0)
    matchBean.setDireTeam(direTeam.toTeamBean)
    matchBean.setRadiantTeam(radiantTeam.toTeamBean)
    matchBean.setMatchScore(basicInfo.getMatchScore())
    matchBean.setNetworth(scala.collection.JavaConversions.seqAsJavaList(netWorth.netWorth.map(new Integer(_))))
    matchBean.setGameNumber(basicInfo.radiantWin + basicInfo.direWin + 1)
    matchBean.setRadianBans(radiantTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean.setRadianPicks(radiantTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDirePicks(direTeam.picks.map(h => new HeroBean(h.id, h.name)))
    matchBean.setDireBans(direTeam.bans.map(h => new HeroBean(h.id, h.name)))
    matchBean
  }

}


