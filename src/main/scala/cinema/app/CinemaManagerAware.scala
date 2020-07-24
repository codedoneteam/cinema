package cinema.app

import akka.actor.typed.scaladsl.adapter._
import cinema.CinemaManager
import cinema.config.ConfigInclude
import cinema.manager.CinemaManager

trait CinemaManagerAware {
  this: SystemAware with ConfigInclude =>
    implicit val cinemaManager: CinemaManager = system.spawn(
      CinemaManager(executorPollSize = implicitConfigBox.config.getInt("cinema.saga-executor-dispatcher.thread-pool-executor.fixed-pool-size")).behavior, "cinema-manager")
}
