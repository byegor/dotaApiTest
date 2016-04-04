package com.eb.schedule.dto

/**
  * Created by Egor on 25.03.2016.
  */
abstract  class SimpleDTO(val id:Int, val name:String = "", logo:Long = -1)

 class HeroDTO(val heroId:Int, val heroName:String = "") extends SimpleDTO(heroId, heroName)

 class ItemDTO(val itemId: Int, val itemName:String = "") extends SimpleDTO(itemId, itemName)

 class LeagueDTO(val leagueId: Int, val leagueName:String = "", val url:String = "") extends SimpleDTO(leagueId, leagueName){
 }
