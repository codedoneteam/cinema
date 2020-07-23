package cinema.log

import akka.actor.typed.scaladsl.ActorContext
import scala.util.{Failure, Success, Try}


class Log(context: ActorContext[_]) {

  def trace[A, B](text: => A)(action: => B): B = {
    if (context.log.isTraceEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def debug[A, B](text: => A)(action: => B): B = {
    if (context.log.isDebugEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def info[A, B](text: => A)(action: => B): B = {
    if (context.log.isInfoEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def warn[A, B](text: => A)(action: => B): B = {
    context.log.warn(toLogMessage(text))
    action
  }

  def error(message: String, t: Throwable): Unit = context.log.error(message, t)

  private def toLogMessage[A](a: => A): String = Try {
    a.toString
  } match {
    case Success(v) => v
    case Failure(e) => e.getMessage
  }
}

object Log {
  def apply(context: ActorContext[_]): Log = new Log(context)
}