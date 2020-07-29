package cinema.config

import com.typesafe.config.{Config => TypeConfig}

trait ConfigInclude extends LoadableConfig {
  implicit val implicitConfigBox: ConfigBox = new ConfigBox {
    override val config: TypeConfig = load(
      property = Option(System.getProperty("config")),
      local = Option(System.getProperty("configPath")),
      fileName = Option(System.getProperty("configFile")))
  }
}