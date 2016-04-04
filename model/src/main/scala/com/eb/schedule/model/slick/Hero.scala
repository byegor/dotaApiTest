package com.eb.schedule.model.slick

import slick.lifted.{Rep, Tag}
import slick.driver.MySQLDriver.api._

/**
  * Created by Egor on 26.03.2016.
  */
case class Hero(id: Int, name: String)

object Hero {

  class HeroTable(_tableTag: Tag) extends Table[Hero](_tableTag, "hero") {
    def * = (id, name) <>((Hero.apply _).tupled, Hero.unapply)

    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({ r => import r._; _1.map(_ => Hero.apply(_1.get, _2.get)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(50, varying = true))
  }

  val table = TableQuery[HeroTable]
}

