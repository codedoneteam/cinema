package cinema.log

import akka.actor.typed.scaladsl.ActorContext

import scala.util.{Failure, Success, Try}


class Log(context: ActorContext[_]) {

  def trace[A](text: A): Unit = if (context.log.isTraceEnabled()) {
    context.log.info(toLogMessage(text))
  }

  def debug[A](text: A): Unit = if (context.log.isDebugEnabled()) {
    context.log.info(toLogMessage(text))
  }

  def info[A](text: A): Unit = if (context.log.isInfoEnabled()) {
    context.log.info(toLogMessage(text))
  }

  def warn[A](text: A): Unit = context.log.warn(toLogMessage(text))

  def error(message: String, t: Throwable): Unit = context.log.error(message, t)

  private def toLogMessage[A](a: A): String = Try {
      a.toString
    } match {
        case Success(v) => v
        case Failure(e) => e.getMessage
    }
}

object Log {
  def apply(context: ActorContext[_]): Log = new Log(context)
}