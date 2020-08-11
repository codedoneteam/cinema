package cinema.log

import cinema.log.processor.LogProcessor
import cinema.log.provider.LoggerProvider

trait AbstractLogger extends LogProcessor {
  this: LoggerProvider =>
    def trace[A, B](text: => A): Unit = {
      if (traceEnabled) {
        traceMessage(toLogMessage(text))
      }
    }

    def traceBlock[A, B](text: => A)(action: => B): B = {
      if (traceEnabled) {
        traceMessage(toLogMessage(text))
      }
      action
    }

    def debug[A, B](text: => A): Unit = {
      if (debugEnabled) {
        debugMessage(toLogMessage(text))
      }
    }

    def debugBlock[A, B](text: => A)(action: => B): B = {
      if (debugEnabled) {
        debugMessage(toLogMessage(text))
      }
      action
    }

    def info[A, B](text: => A): Unit = {
      if (infoEnabled) {
        infoMessage(toLogMessage(text))
      }
    }

    def infoBlock[A, B](text: => A)(action: => B): B = {
      if (infoEnabled) {
        infoMessage(toLogMessage(text))
      }
      action
    }

    def warn[A, B](text: => A): Unit = warnMessage(toLogMessage(text))

    def warnBlock[A, B](text: => A)(action: => B): B = {
      warnMessage(toLogMessage(text))
      action
    }

    def error(message: String, t: Throwable): Unit = errorMessage(message, t)
}
