package com.eb.pulse.crawler.task

/**
  * Created by Egor on 21.04.2017.
  */
trait Task {

  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"

  def runTask():Unit
}
