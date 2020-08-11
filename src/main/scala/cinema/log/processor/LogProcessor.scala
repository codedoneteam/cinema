package cinema.log.processor

import scala.util.{Failure, Success, Try}

trait LogProcessor {
  def toLogMessage[A](message: => A): String = Try { message.toString } match {
    case Success(v) => v
    case Failure(e) => e.getMessage
  }
}
