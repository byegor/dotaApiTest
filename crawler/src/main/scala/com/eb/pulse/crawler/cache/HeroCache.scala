package com.eb.pulse.crawler.cache

import com.eb.pulse.crawler.service.{HeroService, TaskService}
import com.eb.schedule.model.slick.{Hero, UpdateTask}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class HeroCache(val heroService: HeroService, taskService: TaskService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private var cache: mutable.Map[Int, Hero] = getCacheItems

  private def getCacheItems = {
    val heroes: Seq[Hero] = Await.result(heroService.findAll(), Duration.Inf)
    val heroMap: mutable.Map[Int, Hero] = new mutable.HashMap[Int, Hero]()
    heroes.foreach(h => heroMap.put(h.id, h))
    heroMap

  }

  def refresh() = {
    cache = getCacheItems
  }

  def getHero(id: Int): Hero = {
    val hero: Option[Hero] = cache.get(id)
    hero match {
      case Some(h) => h
      case None =>
        log.warn("Couldn't find a hero in case, creating task: " + id)
        taskService.insert(UpdateTask(id, Hero.getClass.getSimpleName, 0))
        new Hero(id, "")
    }
  }


}
