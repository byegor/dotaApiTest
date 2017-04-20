package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.Lookup
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory

/**
  * Created by Egor on 20.04.2017.
  */
class LiveMatchTask extends Lookup{

  private val log = LoggerFactory.getLogger(this.getClass)

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"

  def getLiveLeagueGames(): List[JsonObject] = {
    val response: JsonObject = httpUtils.getResponseAsJson(GET_LIVE_LEAGUE_MATCHES)
    var gamesJson: List[JsonObject] = Nil
    val gamesList: JsonArray = response.getAsJsonObject("result").getAsJsonArray("games")
    for (i <- 0 until gamesList.size()) {
      val game: JsonObject = gamesList.get(i).getAsJsonObject()
      val leagueTier: Int = game.get("league_tier").getAsInt
      if (leagueTier >= 2) {
        gamesJson ::= game
      }
    }
    gamesJson
  }

}
