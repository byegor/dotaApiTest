package com.eb.schedule.utils

import java.io.File
import java.net.URL

import com.eb.schedule.configure.{CoreModule, MysqlModule}
import com.eb.schedule.dto.{HeroDTO, ItemDTO}
import com.eb.schedule.services.{HeroServiceImpl, ItemService}
import com.google.inject.Guice

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.sys.process._

/**
  * Created by Egor on 24.06.2016.
  */
object ItemImageGrabber extends App {


  val injector = Guice.createInjector(new MysqlModule, new CoreModule)
  private val itemServiceImpl: ItemService = injector.getInstance(classOf[ItemService])
  private val items: Seq[ItemDTO] = Await.result(itemServiceImpl.findAll(), Duration.Inf).filter(_.itemId != 0).filter(!_.name.contains("recipe"))
  for (item <- items) {
    fileDownloader("http://media.steampowered.com/apps/dota2/images/items/" + item.name.replace("item_","") + "_eg.png", "i_" + item.itemId + ".png")
  }

  def fileDownloader(url: String, filename: String) = {
    try {
      new URL(url) #> new File(filename) !!
    }catch {
      case e:Throwable => println(url)
    }
  }
}
