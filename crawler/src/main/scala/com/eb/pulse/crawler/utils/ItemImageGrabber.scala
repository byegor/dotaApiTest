package com.eb.pulse.crawler.utils

import java.io.File
import java.net.URL

import com.eb.pulse.crawler.Lookup
import com.eb.pulse.crawler.service.ItemService
import com.eb.schedule.model.slick.Item

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.sys.process._

/**
  * Created by Egor on 24.06.2016.
  */
object ItemImageGrabber extends App {

  private val itemServiceImpl: ItemService = Lookup.itemService
  private val items: Seq[Item] = Await.result(itemServiceImpl.findAll(), Duration.Inf).filter(item => item.id != 0).filter(!_.name.contains("recipe"))
  for (item <- items) {
    fileDownloader("http://media.steampowered.com/apps/dota2/images/items/" + item.name.replace("item_","") + "_lg.png", "i_" + item.id + ".png")
  }


  def fileDownloader(url: String, filename: String) = {
    try {
      new URL(url) #> new File(filename) !!
    }catch {
      case e:Throwable => println(url)
    }
  }
}
