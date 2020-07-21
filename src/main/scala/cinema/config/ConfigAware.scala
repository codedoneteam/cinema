package cinema.config

import com.typesafe.config.{ConfigFactory, Config => TypeConfig}

trait ConfigAware {
  implicit val config: TypeConfig = ConfigFactory.load
}
