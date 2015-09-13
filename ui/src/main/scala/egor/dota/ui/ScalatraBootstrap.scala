package egor.dota.ui

import javax.servlet.ServletContext

import egor.dota.ui.resource.TeamResource
import org.scalatra.LifeCycle

/**
 * Created by Егор on 13.09.2015.
 */
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new TeamResource, "/team")
  }
}