package com.eb.schedule.model.slick

import slick.lifted.{Rep, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by Egor on 13.02.2016.
  */

case class League(id: Int, name: String, description: Option[String] = None, url: Option[String] = None)

object League {

  class LeaguesTable(tag: Tag) extends Table[League](tag, "league") {
    def * = (id, name, description, url) <>((League.apply _).tupled, League.unapply)

    def ? = (Rep.Some(id), Rep.Some(name), description, url).shaped.<>({ r => import r._; _1.map(_ => League.apply(_1.get, _2.get, _3, _4)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(250, varying = true))
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(400, varying = true), O.Default(None))
    val url: Rep[Option[String]] = column[Option[String]]("url", O.Length(100, varying = true), O.Default(None))
  }

  val table = TableQuery[LeaguesTable]
}
