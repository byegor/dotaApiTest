package com.eb.schedule.rest

import javax.servlet.ServletContext

import com.eb.schedule.rest.resource.{GamesResource, TeamResource}
import com.eb.schedule.config.RestLookup
import org.scalatra.LifeCycle

/**
 * Created by Egor on 13.09.2015.
 */
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new TeamResource, "/team")
    context.mount(new GamesResource(RestLookup.scheduleRestService), "/games")
  }
}