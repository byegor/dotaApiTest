package egor.dota.model.utils

import java.util.concurrent.atomic.AtomicLong

import egor.dota.DataBaseHelper
import egor.dota.model.entity.{Team, Hero}
import org.egor.dota.entity.Team

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
