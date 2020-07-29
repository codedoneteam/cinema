package cinema.app

import akka.{actor => classic}
import cinema.config.ConfigInclude

trait SystemAware {
  this: ConfigInclude =>
    implicit val system: classic.ActorSystem = classic.ActorSystem(name = "cinema", config = implicitConfigBox.config)
}