package com.eb.schedule.cache

import com.eb.schedule.RestBasicTest
import com.eb.schedule.dto.TeamDTO

/**
  * Created by Egor on 17.05.2016.
  */
class TeamCacheTest extends RestBasicTest {

  test("CacheItemNotFound") {
    val teamCache: TeamCache = new TeamCache(teamService, taskService)
    val unknownTemaId: Int = 123
    val team: TeamDTO = teamCache.getTeam(unknownTemaId)
    assert(team.name == "")

    whenReady(taskService.getPendingTeamTasks()) {
      seq =>
        assert(1 == seq.size)
        assert(unknownTemaId == seq.head.id)
    }
  }
}
