package cinema.app

import cinema.config.{ConfigAware, ConfigInclude}
import cinema.log.AppLog
import cinema.scheduler.SchedulerAware


trait CinemaAware extends ConfigAware with ConfigInclude with SystemAware with CinemaManagerAware with ActorAware with SchedulerAware with AppLog