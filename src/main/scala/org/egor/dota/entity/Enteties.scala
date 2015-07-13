package org.egor.dota.entity

/**
 * Created by Egor on 12.07.2015.
 */

case class Hero(id: Int, name: String)

case class Team(id: Int, code: String)

case class MatchBasicInfo(id: Long, sequence: Long, radiantWin: Boolean, duration:Int, radiant:List[Hero], dire:List[Hero])

case class MatchResult(id: Long, radiantWin: Boolean, radiant:Team, dire:Team)


