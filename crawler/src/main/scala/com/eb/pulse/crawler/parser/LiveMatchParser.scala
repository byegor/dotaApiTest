package com.eb.pulse.crawler.parser

import com.eb.pulse.crawler.cache.CacheHelper
import com.eb.pulse.crawler.model.{LiveMatch, Player, TeamScoreBoard}
import com.eb.pulse.crawler.service.{NetworthService, TeamService}
import com.eb.schedule.model.SeriesType
import com.eb.schedule.model.slick.Team
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory


/**
  * Created by Egor on 16.04.2017.
  */
class LiveMatchParser(netWorthService: NetworthService, cacheHelper: CacheHelper, teamService: TeamService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  def parse(json: JsonObject): Option[LiveMatch] = {
    try {
      if (json.has("scoreboard")) {
        val matchId: Long = json.get("match_id").getAsLong
        val leagueId: Int = json.get("league_id").getAsInt
        val radiantWin = json.get("radiant_series_wins").getAsByte
        val direWin = json.get("dire_series_wins").getAsByte
        val seriesType = SeriesType.fromCode(json.get("series_type").getAsByte)

        val radiantTeam = parseTeam(json.getAsJsonObject("radiant_team"))
        val direTeam = parseTeam(json.getAsJsonObject("dire_team"))

        val scoreBoard: JsonObject = json.get("scoreboard").getAsJsonObject
        val duration = scoreBoard.get("duration").getAsDouble

        val playerNames = getPlayerNames(json.get("players").getAsJsonArray)
        cacheHelper.putPlayerNames(playerNames)

        val radiantScoreBoard = getTeamScoreBoard(radiantTeam, scoreBoard.get("radiant").getAsJsonObject, playerNames)
        val direScoreBoard = getTeamScoreBoard(direTeam, scoreBoard.get("dire").getAsJsonObject, playerNames)

        val currentNet: Int = radiantScoreBoard.players.map(_.netWorth).sum - direScoreBoard.players.map(_.netWorth).sum

        Some(LiveMatch(matchId, -1, radiantScoreBoard, direScoreBoard, leagueId, currentNet, duration, seriesType, radiantWin, direWin))
      } else {
        None
      }
    } catch {
      case e: Throwable => {
        log.error("couldn't parse live game: " + json, e)
        None
      }
    }
  }


  def parseTeam(json: JsonObject): Team = {
    if (json == null) {
      Team(-1)
    } else {
      val teamId: Int = json.get("team_id").getAsInt
      val name = json.get("team_name").getAsString
      val logo = json.get("team_logo").getAsLong
      val team = Team(teamId, name, logo = logo)
      teamService.upsertTeam(team)
      cacheHelper.putTeam(team)
      team
    }
  }

  def getTeamScoreBoard(team: Team, scoreBoard: JsonObject, playerNames: Map[Int, String]): TeamScoreBoard = {
    val score: Byte = scoreBoard.get("score").getAsByte
    val picks = getHeroesFromPicks(scoreBoard.getAsJsonArray("picks"))
    val bans = getHeroesFromPicks(scoreBoard.getAsJsonArray("bans"))
    val teamPlayers = getTeamPlayers(scoreBoard.get("players").getAsJsonArray, playerNames)
    TeamScoreBoard(team, teamPlayers, picks, bans, score)
  }

  def getPlayerNames(basicPlayerInfo: JsonArray): Map[Int, String] = {
    var playerNames = Map[Int, String]()
    for (i <- 0 until basicPlayerInfo.size()) {
      val player: JsonObject = basicPlayerInfo.get(i).getAsJsonObject
      val team: Int = player.get("team").getAsInt
      if (team < 2) {
        val accountId: Int = player.get("account_id").getAsInt
        val name: String = player.get("name").getAsString
        playerNames = playerNames + (accountId -> name)
      }
    }
    playerNames
  }

  def getTeamPlayers(playerInfo: JsonArray, names: Map[Int, String]): List[Player] = {
    var players: List[Player] = Nil
    for (i <- 0 until playerInfo.size()) {
      val player: JsonObject = playerInfo.get(i).getAsJsonObject
      val accId: Int = player.get("account_id").getAsInt

      val kills = player.get("kills").getAsInt
      val deaths = player.get("death").getAsInt
      val assists = player.get("assists").getAsInt
      val level = player.get("level").getAsInt
      val netWorth = player.get("net_worth").getAsInt
      val hero = player.get("hero_id").getAsInt

      var items: List[Int] = Nil
      for (j <- 0 to 5) {
        items = items :+ player.get("item" + j).getAsInt
      }
      players ::= Player(accId, names(accId), hero, items, level, kills, deaths, assists, netWorth)
    }
    players
  }


  def getHeroesFromPicks(jsonPicks: JsonArray): List[Int] = {
    var picks: List[Int] = Nil
    if (jsonPicks != null) {
      for (i <- 0 until jsonPicks.size()) {
        val pick: JsonObject = jsonPicks.get(i).getAsJsonObject
        val heroId: Int = pick.get("hero_id").getAsInt
        picks :+= heroId
      }
    }
    picks
  }


}
