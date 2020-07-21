package cinema.saga.builder

import java.time.{LocalDateTime, Duration => JDuration}

import scala.concurrent.duration.Duration

object SagaDuration {
  implicit class RichDateTime(val localDateTime: LocalDateTime) extends AnyVal {
    def +(duration: Duration): LocalDateTime = {
      localDateTime.plus(JDuration.ofMillis(duration.toMillis))
    }
  }
}