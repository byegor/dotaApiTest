package com.eb.schedule.rest

import javax.servlet.ServletContext

import com.eb.schedule.rest.resource.{DebugResource, GamesResource}
import org.scalatra.LifeCycle

/**
 * Created by Egor on 13.09.2015.
 */
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new DebugResource, "/debug")
    context.mount(new GamesResource, "/games")
  }
}