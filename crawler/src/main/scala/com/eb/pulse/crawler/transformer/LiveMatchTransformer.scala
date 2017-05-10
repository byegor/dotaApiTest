package com.eb.pulse.crawler.transformer

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.cache.CacheHelper
import com.eb.pulse.crawler.model.{LiveMatch, Player, TeamScoreBoard}
import com.eb.schedule.shared.bean.{HeroBean, Item, Match, TeamBean}

import scala.collection.JavaConversions._


/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
object LiveMatchTransformer {

  val cacheHelper: CacheHelper = Lookup.cacheHelper
  val networthService = Lookup.netWorthService

  def transform(liveMatch: LiveMatch): Match = {
    val radiantTeam = transformTeam(liveMatch.radiantTeamBoard)
    val direTeam = transformTeam(liveMatch.direTeamBoard)
    val netWorth = networthService.findByMatchId(liveMatch.matchId)
    val matchBean = new Match()
    matchBean.setMatchId(liveMatch.matchId)
    matchBean.setDuration(getDuration(liveMatch.duration.toInt))
    matchBean.setMatchStatus(0)
    matchBean.setRadiantTeam(radiantTeam)
    matchBean.setDireTeam(direTeam)
    matchBean.setMatchScore(liveMatch.radiantTeamBoard.score + " - " + liveMatch.direTeamBoard.score)
    matchBean.setNetworth(seqAsJavaList(netWorth.netWorth.split(",").toList.map(new Integer(_))))
    matchBean.setGameNumber(-1) //todo do i need gameNumber in match bean
    matchBean.setRadianPicks(seqAsJavaList(liveMatch.radiantTeamBoard.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setRadianBans(seqAsJavaList(liveMatch.radiantTeamBoard.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setDirePicks(seqAsJavaList(liveMatch.direTeamBoard.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean.setDireBans(seqAsJavaList(liveMatch.direTeamBoard.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))))
    matchBean
  }


  def transformTeam(teamScoreBoard: TeamScoreBoard): TeamBean = {
    val players = transformPlayers(teamScoreBoard.players)
    val team = teamScoreBoard.team
    new TeamBean(team.id, team.name, team.tag, team.logo, players)
  }

  def transformPlayers(players: List[Player]): Seq[com.eb.schedule.shared.bean.Player] = {
    players.map(p => {
      val hero = cacheHelper.getHero(p.hero)
      val items = p.items.map(cacheHelper.getItem).map(item => new Item(item.id, item.name))

      val player = new com.eb.schedule.shared.bean.Player()
      player.setAccountId(p.accountId)
      player.setName(p.name)
      player.setHero(new HeroBean(hero.id, hero.name))
      player.setItems(seqAsJavaList(items))
      player.setLevel(p.level)
      player.setKills(p.kills)
      player.setDeaths(p.death)
      player.setAssists(p.assists)
      player.setNetWorth(p.netWorth)
      player
    })
  }

  def getDuration(duration:Int) = {
    val minutes: Int = duration / 60
    val seconds: Int = duration - minutes * 60
    minutes + ":" + "%02d".format(seconds)
  }

}
