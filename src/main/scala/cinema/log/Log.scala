package cinema.log

import akka.actor.typed.scaladsl.ActorContext
import cinema.log.provider.ContextLogger
import org.slf4j.Logger


class Log(context: ActorContext[_]) extends AbstractLogger with ContextLogger {
  override lazy val logger: Logger = context.log
}

object Log {
  def apply(context: ActorContext[_]): Log = new Log(context)
}