package cinema.log.provider

import org.slf4j.Logger

trait ContextLogger extends LoggerProvider {
    val logger: Logger

    override def traceEnabled: Boolean = logger.isTraceEnabled

    override def debugEnabled: Boolean = logger.isDebugEnabled

    override def infoEnabled: Boolean = logger.isInfoEnabled

    override def traceMessage[A, B](message: String): Unit = logger.trace(message)

    override def debugMessage[A, B](message: String): Unit = logger.debug(message)

    override def infoMessage[A, B](message: String): Unit = logger.info(message)

    override def warnMessage[A, B](message: String): Unit = logger.warn(message)

    override def errorMessage(message: String, t: Throwable): Unit = logger.error(message)
}