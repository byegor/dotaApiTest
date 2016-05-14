package com.eb.schedule.crawler


object CrawlerUrls {

   val GET_TEAM_BY_ID: String = "https://api.steampowered.com/IDOTA2Match_570/GetTeamInfoByTeamID/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&teams_requested=1&start_at_team_id="
   val GET_TEAM_LOGO: String = "http://api.steampowered.com/ISteamRemoteStorage/GetUGCFileDetails/v1/?key=9EBD51CD27F27324F1554C53BEDA17C3&appid=570&ugcid="


  val GET_LEAGUES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLeagueListing/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"
  val GET_LIVE_LEAGUE_MATCHES: String = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  val GET_ITEMS: String = "https://api.steampowered.com/IEconDOTA2_570/GetGameItems/v0001/?key=9EBD51CD27F27324F1554C53BEDA17C3"

  val GET_MATCH_DETAILS: String = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/v001/?key=9EBD51CD27F27324F1554C53BEDA17C3&match_id="


}
