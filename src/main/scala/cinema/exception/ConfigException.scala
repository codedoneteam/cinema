package cinema.exception

class ConfigException(map: Map[String, _]) extends RuntimeException(map.toString)