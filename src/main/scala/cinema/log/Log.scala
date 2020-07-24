package cinema.log

import akka.actor.typed.scaladsl.ActorContext

import scala.util.{Failure, Success, Try}


class Log(context: ActorContext[_]) {

  def trace[A, B](text: => A): Unit = {
    if (context.log.isTraceEnabled()) {
      context.log.info(toLogMessage(text))
    }
  }

  def traceBlock[A, B](text: => A)(action: => B): B = {
    if (context.log.isTraceEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def debug[A, B](text: => A): Unit = {
    if (context.log.isDebugEnabled()) {
      context.log.info(toLogMessage(text))
    }
  }

  def debugBlock[A, B](text: => A)(action: => B): B = {
    if (context.log.isDebugEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def info[A, B](text: => A): Unit = {
    if (context.log.isInfoEnabled()) {
      context.log.info(toLogMessage(text))
    }
  }

  def infoBlock[A, B](text: => A)(action: => B): B = {
    if (context.log.isInfoEnabled()) {
      context.log.info(toLogMessage(text))
    }
    action
  }

  def warn[A, B](text: => A): Unit = context.log.warn(toLogMessage(text))

  def warnBlock[A, B](text: => A)(action: => B): B = {
    context.log.warn(toLogMessage(text))
    action
  }

  def error(message: String, t: Throwable): Unit = context.log.error(message, t)

  private def toLogMessage[A](message: => A): String = Try { message.toString } match {
    case Success(v) => v
    case Failure(e) => e.getMessage
  }
}

object Log {
  def apply(context: ActorContext[_]): Log = new Log(context)
}