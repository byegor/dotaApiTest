package com

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.config.RestLookup

/**
  * Created by Egor on 18.04.2016.
  */
object RestJobRunner {
  val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2)

  def start() = {
    Thread.sleep(2000) //have no idea now why it helps to start
    RestLookup.restartProcessor.process()
    executor.scheduleAtFixedRate(RestLookup.liveGameProcessor, 0, 60, TimeUnit.SECONDS)
  }


}
