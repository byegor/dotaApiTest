package com.eb.schedule.dto

/**
  * Created by Egor on 26.03.2016.
  */
//todo check if Long
class PlayerDTO(val accountId: Int) {
  var name = ""
  var hero: HeroDTO = _
  var items: List[ItemDTO] = Nil
  var level = 1
  var kills = 0
  var deaths = 0
  var assists = 0
  var netWorth = 0
}
