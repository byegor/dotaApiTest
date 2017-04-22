package com.eb.pulse.crawler.model

import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.slick._

/**
  * Created by Egor on 13.03.2016.
  */
//todo do we need current Net worth
case class LiveMatch(matchId: Long, scheduledGameId: Int, radiantTeamBoard: TeamScoreBoard, direTeamBoard: TeamScoreBoard, league: Int, currentNet: Int, duration: Double, radiantScore: Int,
                     direScore: Int, seriesType: SeriesType, winByRadiant: Int, winByDire: Int)

case class FinishedMatch(matchId: Long, startTime: Long, duration: Int, radiantWin: Boolean, radiantTeam: TeamScoreBoard, direTeam: TeamScoreBoard, league: Int,
                         netWorth: NetWorth, winByRadiant: Int = 0, winByDire: Int = 0)

case class Player(accountId: Int, name: String, hero: Int, items: List[Int], level: Int, kills: Int, death: Int, assists: Int, netWorth: Int)

case class TeamScoreBoard(team: Team, players: List[Player] = Nil, picks: List[Int] = Nil, bans: List[Int] = Nil, score: Int, towerStatus: Int = -1, barrackStatus: Int = -1)
