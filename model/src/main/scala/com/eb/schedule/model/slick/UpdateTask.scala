package com.eb.schedule.model.slick

import slick.driver.MySQLDriver.api._
import slick.lifted.{Rep, Tag}

/**
  * Created by Egor on 23.03.2016.
  */
case class UpdateTask(id: Long, classname: String, result: Byte)

object UpdateTask {

  class UpdateTaskTable(_tableTag: Tag) extends Table[UpdateTask](_tableTag, "update_task") {
    def * = (id, classname, result) <>((UpdateTask.apply _).tupled, UpdateTask.unapply)

    val id: Rep[Long] = column[Long]("id")
    val classname: Rep[String] = column[String]("classname", O.Length(150, varying = true))
    val result: Rep[Byte] = column[Byte]("result", O.Default(0))

    val index1 = index("id_classname_select", (id, classname))
  }

  val table = TableQuery[UpdateTaskTable]

}