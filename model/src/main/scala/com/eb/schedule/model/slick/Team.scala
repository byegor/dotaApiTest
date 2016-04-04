package com.eb.schedule.model.slick

import slick.lifted.{Rep, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by Egor on 23.03.2016.
  */
case class Team(id: Int, name: String, tag: String, logo: Long)

object Team {

  class TeamsTable(_tableTag: Tag) extends Table[Team](_tableTag, "team") {
    def * = (id, name, tag, logo) <>((Team.apply _).tupled, Team.unapply)

    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(tag), Rep.Some(logo)).shaped.<>({ r => import r._; _1.map(_ => Team.apply(_1.get, _2.get, _3.get, _4.get)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(50, varying = true))
    val tag: Rep[String] = column[String]("tag", O.Length(20, varying = true))
    val logo: Rep[Long] = column[Long]("logo", O.Length(20))
  }

  val table = TableQuery[TeamsTable]
}
