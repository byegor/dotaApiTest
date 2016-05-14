package com.eb.schedule.live

import com.eb.schedule.dto.{CurrentGameDTO, ScheduledGameDTO}
import com.eb.schedule.model.MatchStatus
import com.eb.schedule.model.services.ScheduledGameService
import com.google.inject.Inject

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Egor on 22.04.2016.
  */
//todo test it, add logs
class RestartProcessor @Inject()(val liveGameProcessor: LiveGameProcessor, scheduledGameService: ScheduledGameService) {

  def process(): Unit = {
    val liveGames: Future[Seq[ScheduledGameDTO]] = scheduledGameService.getScheduledGamesByStatus(MatchStatus.LIVE)
    liveGameProcessor.run()
    val storedGames: Seq[ScheduledGameDTO] = Await.result(liveGames, Duration.Inf)
    val currentLive: Iterable[CurrentGameDTO] = GameContainer.getLiveMatches()

    val stillRunning: Seq[ScheduledGameDTO] = for (stored <- storedGames;
                                                   current <- currentLive
                                                   if isSameGame(stored, current)
    ) yield stored

    val finishedGames: Seq[ScheduledGameDTO] = storedGames.diff(stillRunning)
    finishedGames.foreach(game => scheduledGameService.updateStatus(game.id, MatchStatus.FINISHED))
  }

  def isSameGame(storedGame: ScheduledGameDTO, livaGame: CurrentGameDTO): Boolean = {
    storedGame.league.leagueId == livaGame.basicInfo.league.leagueId &&
      ((storedGame.direTeam.id == livaGame.direTeam.id && storedGame.radiantTeam.id == livaGame.radiantTeam.id) ||
        (storedGame.direTeam.id == livaGame.radiantTeam.id && storedGame.radiantTeam.id == livaGame.direTeam.id))
  }

}
