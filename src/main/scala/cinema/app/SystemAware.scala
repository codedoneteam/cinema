package cinema.app

import akka.{actor => classic}
import cinema.config.ConfigAware

trait SystemAware {
  this: ConfigAware =>
    implicit val system: classic.ActorSystem = classic.ActorSystem(name = "cinema", config = config)
}