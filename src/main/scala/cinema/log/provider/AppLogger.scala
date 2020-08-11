package cinema.log.provider

import akka.event.LoggingAdapter

trait AppLogger extends LoggerProvider {
    val loggingAdapter: LoggingAdapter
  
    override def traceEnabled: Boolean = false

    override def debugEnabled: Boolean = loggingAdapter.isDebugEnabled

    override def infoEnabled: Boolean = loggingAdapter.isInfoEnabled

    override def traceMessage[A, B](message: String): Unit = loggingAdapter.warning("Trace level not supported!")

    override def debugMessage[A, B](message: String): Unit = loggingAdapter.debug(message)

    override def infoMessage[A, B](message: String): Unit = loggingAdapter.debug(message)

    override def warnMessage[A, B](message: String): Unit = loggingAdapter.warning(message)

    override def errorMessage(message: String, t: Throwable): Unit = loggingAdapter.error(message, t)
}