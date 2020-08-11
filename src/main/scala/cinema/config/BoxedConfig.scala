package cinema.config

import com.typesafe.config.{Config => TypeConfig}

trait BoxedConfig {
  val config: TypeConfig
}