package cinema.app

import akka.actor.typed.scaladsl.adapter._
import cinema.CinemaManager
import cinema.config.ConfigAware
import cinema.manager.CinemaManager

trait CinemaManagerAware {
  this: SystemAware with ConfigAware =>
    implicit val cinemaManager: CinemaManager = system.spawn(
      CinemaManager(executorPollSize = config.getInt("cinema.saga-executor-dispatcher.thread-pool-executor.fixed-pool-size")).behavior, "cinema-manager")
}
