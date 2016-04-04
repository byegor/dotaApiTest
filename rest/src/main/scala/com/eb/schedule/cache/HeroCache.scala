package com.eb.schedule.cache

import com.eb.schedule.dto.HeroDTO
import com.eb.schedule.services.HeroService
import com.google.inject.Inject
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by Egor on 26.03.2016.
  */
class HeroCache @Inject()(val heroService: HeroService) {

  private val log = LoggerFactory.getLogger(this.getClass)

  private val unknownHero: HeroDTO = new HeroDTO(-1)

  private val cache: mutable.Map[Int, HeroDTO] = {
    val heroes: Seq[HeroDTO] = Await.result(heroService.findAll(), Duration.Inf)
    val heroMap: mutable.Map[Int, HeroDTO] = new mutable.HashMap[Int, HeroDTO]()
    heroes.foreach(h => heroMap.put(h.id, h))
    heroMap
  }

  def getHero(id: Int): HeroDTO = {
    val option: Option[HeroDTO] = cache.get(id)
    if (option.isDefined) {
      option.get
    } else {
      log.error("Couldn't find a hero: " + id)
      unknownHero
    }
  }


}
