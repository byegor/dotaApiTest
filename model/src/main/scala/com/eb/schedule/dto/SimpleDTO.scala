package com.eb.schedule.dto

/**
  * Created by Egor on 25.03.2016.
  */
abstract  class SimpleDTO(val id:Int, val name:String = "", logo:Long = -1)

 case class HeroDTO(heroId:Int, heroName:String = "") extends SimpleDTO(heroId, heroName)

 case class ItemDTO(itemId: Int, itemName:String = "") extends SimpleDTO(itemId, itemName)

 case class LeagueDTO(leagueId: Int, leagueName:String = "", url:String = "") extends SimpleDTO(leagueId, leagueName){
 }
