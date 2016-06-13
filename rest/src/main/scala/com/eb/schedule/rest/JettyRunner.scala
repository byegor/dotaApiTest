package com.eb.schedule.rest

import com.RestJobRunner
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
  * Created by Egor on 13.09.2015.
  */
object JettyRunner extends App {

  RestJobRunner.start()

  val port = if (System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

  val server = new Server(port)
  val context = new WebAppContext()
  context setContextPath "/"
  context.setInitParameter(ScalatraListener.LifeCycleKey, "com.eb.schedule.rest.ScalatraBootstrap")
  context.setResourceBase("src/main/webapp")
  context.addEventListener(new ScalatraListener)
  context.addServlet(classOf[DefaultServlet], "/")

  server.setHandler(context)

  server.start()
  server.join()


}
