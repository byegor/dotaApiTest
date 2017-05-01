package com.eb.pulse.crawler.transformer

import com.eb.pulse.crawler.cache.CacheHelper
import com.eb.pulse.crawler.model.{FinishedMatch, Player, TeamScoreBoard}
import com.eb.pulse.crawler.service.NetworthService
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.shared.bean.{HeroBean, Item, Match, TeamBean}

/**
  * Created by Iegor.Bondarenko on 01.05.2017.
  */
object FinishedMatchToTransformer {

  val cacheHelper: CacheHelper = _
  val networthService:NetworthService =_

  def transform(finishedMatch: FinishedMatch): Match = {
    val radiantTeam = transformTeam(finishedMatch.radiantTeam)
    val direTeam = transformTeam(finishedMatch.direTeam)
    networthService
    new Match(
finishedMatch.matchId,
      finishedMatch.startTime,
      getDuration(finishedMatch.duration),
      MatchStatus.FINISHED.status,
      radiantTeam,
      direTeam,
      finishedMatch.netWorth.netWorth.split(",").map(Integer.parseInt(_)),
      -1, //todo do i need gameNumber
      finishedMatch.radiantTeam.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name)),
      finishedMatch.radiantTeam.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name)),
      finishedMatch.direTeam.picks.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name)),
      finishedMatch.direTeam.bans.map(cacheHelper.getHero(_)).map(hero => new HeroBean(hero.id, hero.name))

    )
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
      new com.eb.schedule.shared.bean.Player(p.accountId, p.name, new HeroBean(hero.id, hero.name), items, p.level, p.kills, p.death, p.assists, p.netWorth)
    })
  }

  def getDuration(duration:Int) = {
    val minutes: Int = duration / 60
    val seconds: Int = duration - minutes * 60
    minutes + ":" + "%02d".format(seconds)
  }

}
