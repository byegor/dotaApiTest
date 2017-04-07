package com

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

import com.eb.schedule.config.RestLookup

/**
  * Created by Egor on 18.04.2016.
  */
object RestJobRunner {
  val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  def start() = {
    Thread.sleep(2000) //have no idea why it helps to start
    RestLookup.restartProcessor.process()
    executor.scheduleAtFixedRate(RestLookup.liveGameProcessor, 0, 45, TimeUnit.SECONDS)
  }


}
