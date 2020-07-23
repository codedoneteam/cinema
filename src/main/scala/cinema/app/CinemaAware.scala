package cinema.app

import cinema.after.AfterAware
import cinema.config.ConfigAware

trait CinemaAware extends ConfigAware with SystemAware with CinemaManagerAware with ActorAware with AfterAware