package com.eb.schedule.model.slick

import slick.driver.MySQLDriver.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 26.03.2016.
  */
case class Item(id: Int, name: String)

object Item {

  class ItemTable(_tableTag: Tag) extends Table[Item](_tableTag, "item") {
    def * = (id, name) <>((Item.apply _).tupled, Item.unapply)

    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({ r => import r._; _1.map(_ => Item.apply(_1.get, _2.get)) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(50, varying = true))
  }

  val table = TableQuery[ItemTable]
}

