package cinema.config

import com.typesafe.config.{Config => TypeConfig}

trait ConfigBox {
  val config: TypeConfig
}