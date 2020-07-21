package cinema.app

import cinema.config.ConfigAware

trait CinemaAware extends ConfigAware with SystemAware with CinemaManagerAware with ActorAware