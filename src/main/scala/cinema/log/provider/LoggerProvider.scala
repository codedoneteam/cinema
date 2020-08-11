package cinema.log.provider

trait LoggerProvider {
  def traceEnabled: Boolean

  def debugEnabled: Boolean

  def infoEnabled: Boolean

  def traceMessage[A, B](message: String): Unit

  def debugMessage[A, B](message: String): Unit

  def infoMessage[A, B](message: String): Unit

  def warnMessage[A, B](message: String): Unit

  def errorMessage(message: String, t: Throwable): Unit
}
