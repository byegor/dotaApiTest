package com.eb.pulse.crawler.model

import java.sql.Timestamp

import com.eb.schedule.model.slick._
import com.eb.schedule.model.{MatchStatus, SeriesType}

/**
  * Created by Egor on 13.03.2016.
  */
/*case class NetWorthDTO(matchId: Long, netWorth: List[Int] = Nil)

case class HeroDTO(heroId: Int, heroName: String = "")

case class ItemDTO(itemId: Int, itemName: String = "")*/

case class LiveMatch(matchId: Long, scheduledGameId: Int, radiantTeam: TeamScoreBoard, direTeam: TeamScoreBoard, league: Int, currentNet: Int, duration: Double, radiantScore: Int,
                     direScore: Int, seriesType: SeriesType, winByRadiant: Int, winByDire: Int)

case class FinishedMatch(matchId: Long, startTime: Long, duration: Int, radiantWin: Boolean, radiantTeam: TeamScoreBoard, direTeam: TeamScoreBoard, league: League,
                         netWorth: NetWorth, gameNumber: Int, winByRadiant: Int = 0, winByDire: Int = 0)

case class ScheduledGameDTO(id: Int, radiantTeam: Team, direTeam: Team, league: League, seriesType: SeriesType, startDate: Timestamp, matchStatus: MatchStatus)

case class Player(accountId: Int, name: String, hero: Int, items: List[Int], level: Int, kills: Int, death: Int, assists: Int, netWorth: Int)

case class TeamScoreBoard(team: Team, players: List[Player] = Nil, picks: List[Int] = Nil, bans: List[Int] = Nil, score: Int, towerStatus: Int = -1, barrackStatus: Int = -1)
