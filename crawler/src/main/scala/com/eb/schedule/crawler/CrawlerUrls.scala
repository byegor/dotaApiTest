package com.eb.schedule.crawler


object CrawlerUrls {

   val GET_TEAM_BY_ID: String = "https://api.steampowered.com/IDOTA2Match_570/GetTeamInfoByTeamID/v001/?key=D998B8BDFA96FAA893E52903D6A77EEA&teams_requested=1&start_at_team_id="
   val GET_TEAM_LOGO: String = "http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=D998B8BDFA96FAA893E52903D6A77EEA&appid=570&ugcid="


  val GET_LEAGUES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLeagueListing/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"
  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"

  val GET_ITEMS: String = "https://api.steampowered.com/IEconDOTA2_570/GetGameItems/v0001/?key=D998B8BDFA96FAA893E52903D6A77EEA"

  val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=D998B8BDFA96FAA893E52903D6A77EEA&match_id="


}
