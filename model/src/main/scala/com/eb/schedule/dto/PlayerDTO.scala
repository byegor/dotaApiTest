package com.eb.schedule.dto

import com.eb.schedule.shared.bean.{HeroBean, Item, Player}
import scala.collection.JavaConversions._

/**
  * Created by Egor on 26.03.2016.
  */
class PlayerDTO(val accountId: Int) {
  var name = ""
  var hero: HeroDTO = _
  var items: List[ItemDTO] = Nil
  var level = 1
  var kills = 0
  var deaths = 0
  var assists = 0
  var netWorth = 0

  def toPlayer(): Player = {
    val player = new Player
    player.setName(name)
    player.setHero(new HeroBean(hero.id, hero.name))

    player.setItems(items.map(i => new Item(i.id, i.name)))
    player.setAssists(assists)
    player.setLevel(level)
    player.setKills(kills)
    player.setDeaths(deaths)
    player.setNetWorth(netWorth)
    player
  }
}
