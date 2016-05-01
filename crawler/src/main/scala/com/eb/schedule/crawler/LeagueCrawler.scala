package com.eb.schedule.crawler

import com.eb.schedule.crawler.CrawlerUrls._
import com.eb.schedule.dto.{LeagueDTO, TaskDTO}
import com.eb.schedule.model.Finished
import com.eb.schedule.model.services.{LeagueService, UpdateTaskService}
import com.eb.schedule.model.slick.{League, UpdateTask}
import com.eb.schedule.utils.HttpUtils
import com.google.gson.{JsonArray, JsonObject}
import com.google.inject.Inject
import org.json.{JSONArray, JSONObject}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class LeagueCrawler @Inject()(leagueService: LeagueService, taskService: UpdateTaskService, httpUtils: HttpUtils) extends Runnable {

  private val log = LoggerFactory.getLogger(this.getClass)

  def run() {
    val tasks: Future[Seq[TaskDTO]] = taskService.getPendingLeagueTasks()
    val result: Seq[TaskDTO] = Await.result(tasks, Duration.Inf)
    val ids: Seq[Long] = result.map(_.id)
    val steamItems: JsonArray = getItemsInfoFromSteam()
    for (i <- 0 until steamItems.size()) {
      val itemJson: JsonObject = steamItems.get(i).getAsJsonObject

      val leagueId: Int = itemJson.get("leagueid").getAsInt
      if (ids.contains(leagueId)) {
        log.info("find league to process: " + leagueId)
        leagueService.insert(new LeagueDTO(leagueId, parseName(itemJson.get("name").getAsString), itemJson.get("tournament_url").getAsString))
        taskService.update(new UpdateTask(leagueId, League.getClass.getSimpleName, Finished.status.toByte))
      }
    }
  }


  def parseName(name: String): String = {
    name
    //    name.replace("item_", "").replace("_", " ")
  }


  def getItemsInfoFromSteam(): JsonArray = {
    val teamInfo: JsonObject = httpUtils.getResponseAsJson(GET_LEAGUES)
    val result: JsonObject = teamInfo.getAsJsonObject("result")
    val items: JsonArray = result.getAsJsonArray("leagues")
    items
  }

}
