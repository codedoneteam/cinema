package cinema.config

import com.typesafe.config.{Config => TypeConfig}

trait ConfigAware extends LoadableConfig {
  implicit val config: TypeConfig = load(property = Option(System.getProperty("config")),
                                         local = Option(System.getProperty("configPath")),
                                         fileName = Option(System.getProperty("configFile"))
  )
}