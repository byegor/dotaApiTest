package org.egor.dota.utils

import egor.dota.DataBaseHelper
import org.egor.dota.entity.{Team, Hero}

/**
 * Created by Егор on 13.07.2015.
 */
object TeamUtils {


   def getOrCreateIdForEachTeam(team: List[Hero]): Team = {
    val ids: Array[Integer] = Array.ofDim(5)

    val teamIds: List[Int] = team.map(h => h.id).sorted
    val teamCode: String = teamIds.mkString("_")
    var teamIdByCode: Int = DBUtils.getTeamIdByCode(teamCode)
    if (teamIdByCode == -1) {
      teamIdByCode = DBUtils.createTeam(teamCode)
    }
    new Team(teamIdByCode, teamCode)
  }
}
