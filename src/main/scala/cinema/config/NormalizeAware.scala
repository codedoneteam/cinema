package cinema.config

trait NormalizeAware {
  def normalizePath(path: String): String = {
    val upperList = ('A' to 'Z').map(_.toString)
    val list = path.split("")
    (List(list.head.toLowerCase) ++ list.tail).map {
      case s if upperList.contains(s) => "-" + s.toLowerCase
      case s => s
    }
      .reduce(_ + _)
  }

  def normalizeKey(key: String): String =  if (key.length > 2) {
    val keyList = key.split("")
    val normalizedKey = (2 to key.length).map(i => keyList.take(i - 1).last -> keyList.take(i).last)
      .map { case ("-", s) => s.toUpperCase
      case (_, "-") => ""
      case (_, s) => s
      }
      .reduce(_ + _)
    keyList.head + normalizedKey
  } else {
    key
  }
}
