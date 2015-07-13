package org.egor.dota

import org.egor.dota.job.RetrieveMatchStatisticsBasedOnSequenceJob

/**
 * Created by Егор on 13.07.2015.
 */
object DotaApiStarter extends App {

  new Thread(new RetrieveMatchStatisticsBasedOnSequenceJob(0, 1048875935l)).start()
//  new Thread(new RetrieveMatchStatisticsBasedOnSequenceJob(1, 1050875935l)).start()
}
