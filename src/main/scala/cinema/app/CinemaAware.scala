package cinema.app

import cinema.config.ConfigAware
import cinema.scheduler.SchedulerAware

trait CinemaAware extends ConfigAware with SystemAware with CinemaManagerAware with ActorAware with SchedulerAware