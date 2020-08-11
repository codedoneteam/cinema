package cinema.log

import akka.event.LoggingAdapter
import cinema.app.SystemAware
import cinema.log.provider.AppLogger

trait AppLog extends AbstractLogger with AppLogger {
  this: SystemAware =>
   override lazy val loggingAdapter: LoggingAdapter = implicitAkkaSystem.log
}