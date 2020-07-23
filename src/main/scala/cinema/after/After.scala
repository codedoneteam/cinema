package cinema.after

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors._

import scala.concurrent.duration.{Duration, FiniteDuration, _}

object After {

  sealed trait After
  case class Delayed(duration: Duration, action: () => Unit) extends After
  case class Run(action: () => Unit) extends After

  def apply(): Behavior[After] = withTimers { timers =>
    receive[After]((_, message) => {
      message match {
        case Delayed(duration, action) =>
          timers.startSingleTimer(Run(action), FiniteDuration(duration.toMillis, MILLISECONDS))
          same
        case Run(action) =>
          action()
          stopped
      }
    })
  }
}