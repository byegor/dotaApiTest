package com.eb.schedule.dao

import com.eb.schedule.model.db.DB
import com.eb.schedule.model.slick.Item
import com.eb.schedule.model.slick.Item.ItemTable
import com.google.inject.Inject
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcBackend

import scala.concurrent.Future

/**
  * Created by Egor on 26.03.2016.
  */
trait ItemRepository {

  def findById(id: Int): Future[Item]

  def exists(id: Int): Future[Boolean]

  def findAll(): Future[Seq[Item]]

  def insert(items: Seq[Item])
}

class ItemRepositoryImpl @Inject()(database: DB) extends ItemRepository {
  val db: JdbcBackend#DatabaseDef = database.db

  lazy val item = Item.table

  def filterQuery(id: Int): Query[ItemTable, Item, Seq] = item.filter(_.id === id)

  def findById(id: Int): Future[Item] =
    db.run(filterQuery(id).result.head)

  def exists(id: Int): Future[Boolean] =
    db.run(filterQuery(id).exists.result)

  def findAll(): Future[Seq[Item]] = {
    db.run(item.map(h => h).result)
  }

  def insert(items: Seq[Item]): Unit ={
    db.run(item ++= items)
  }
}
