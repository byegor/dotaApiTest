package com.eb.pulse.crawler.task

import com.eb.pulse.crawler.cache.HeroCache
import com.eb.pulse.crawler.service.{HeroService, TaskService}
import com.eb.schedule.crawler.CrawlerUrls.GET_HEROES
import com.eb.schedule.model.slick.Hero
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/**
  * Created by Iegor.Bondarenko on 04.05.2017.
  */
class DownloadHeroTask(taskService: TaskService, httpUtils: HttpUtils, heroService: HeroService, heroCache: HeroCache) {

  //todo nextVersion: download icon
  def execute(): Unit = {
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
