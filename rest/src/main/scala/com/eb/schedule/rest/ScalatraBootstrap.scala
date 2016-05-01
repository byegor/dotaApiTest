package com.eb.schedule.rest

import javax.servlet.ServletContext

import com.eb.schedule.rest.resource.TeamResource
import org.scalatra.LifeCycle

/**
 * Created by Egor on 13.09.2015.
 */
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new TeamResource, "/team")
  }
}