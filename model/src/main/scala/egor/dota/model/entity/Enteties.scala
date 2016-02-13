package egor.dota.model.entity

import java.util.Date


case class Hero(id: Int, name: String)

case class Team(id: Int, code: String)

case class MatchBasicInfo(id: Long, sequence: Long, radiantWin: Boolean, duration: Int, radiant: List[Hero], dire: List[Hero])

case class MatchResult(id: Long, radiantWin: Boolean, radiant: Team, dire: Team)

case class MatchDetails(id: Long, radiantWin: Boolean, duration: Int, radiant: Team, dire: Team, accounts: List[Int])


case class TeamInfo(id: Int, name: String, tag: String)

case class League(id: Int, name: String, dscr: String, url:String)

case class Match(matchId: Long, radiantId:Int, direId:Int, leagueid:Int, seriesType:Byte, radiantWin:Byte, gameNumber:Byte)

case class Game(id:Int, radiantId:Int, direId:Int, matchId: Long, leagueId:Int, startTime:Date, status:MatchStatus)

