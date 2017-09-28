package com.eb.pulse.crawler.data.task

import com.eb.pulse.crawler.CrawlerUrls.GET_HEROES
import com.eb.pulse.crawler.data.cache.HeroCache
import com.eb.pulse.crawler.data.service.{HeroService, TaskService}
import com.eb.schedule.model.slick.Hero
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/**
  * Created by Iegor.Bondarenko on 04.05.2017.
  */
class DownloadHeroTask(taskService: TaskService, httpUtils: HttpUtils, heroService: HeroService, heroCache: HeroCache) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)


  //todo nextVersion: download icon
  def run(): Unit = {
    try {
      val pendingTasks = taskService.getPendingTasks(Hero.getClass.getSimpleName)
      val heroesId = pendingTasks.map(seq => seq.map(_.id))
      heroesId.onComplete {
        case Success(ids) =>
          if (ids.nonEmpty) {
            val heroesJson = getHeroesFromSteam()
            for (i <- 0 until heroesJson.size()) {
              val json = heroesJson.get(i).getAsJsonObject
              val heroId = json.get("id").getAsInt
              if (ids.contains(heroId)) {
                val heroName = json.get("name").getAsString
                val insertHero = heroService.insertHero(Hero(heroId, heroName.replace("npc_dota_hero_", "")))
                insertHero.onComplete {
                  case Success(ignore) => heroCache.refresh()
                }

              }
            }
          }
      }
    } catch {
      case e: Throwable => log.error("Issue in running task", e)
    }

  }


  def getHeroesFromSteam(): JsonArray = {
    val teamInfo: JsonObject = httpUtils.getResponseAsJson(GET_HEROES)
    val result: JsonObject = teamInfo.getAsJsonObject("result")
    if (result.has("status") && result.get("status").getAsInt == 200) {
      result.getAsJsonArray("heroes")
    } else {
      new JsonArray
    }
  }
}
