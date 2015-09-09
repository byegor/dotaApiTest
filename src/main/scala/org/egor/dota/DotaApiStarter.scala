package org.egor.dota

import org.egor.dota.job.{RetrieveMatchStatisticsBasedOnMatchId, RetrieveUserAndMatchBasedOnUser}

/**
 * Created by Егор on 13.07.2015.
 */
object DotaApiStarter extends App {
  new Thread(new RetrieveUserAndMatchBasedOnUser).start()
  new Thread(new RetrieveMatchStatisticsBasedOnMatchId).start()
}
