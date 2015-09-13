package egor.dota.harvester

import egor.dota.harvester.job.{RetrieveMatchStatisticsBasedOnMatchId, RetrieveUserAndMatchBasedOnUser}

/**
 * Created by Егор on 13.07.2015.
 */
object HarvesterStarter extends App {
  new Thread(new RetrieveUserAndMatchBasedOnUser).start()
  new Thread(new RetrieveMatchStatisticsBasedOnMatchId).start()
}
