package cinema.scheduler

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors._

import scala.concurrent.duration.{Duration, FiniteDuration, _}

object Scheduler {

  sealed trait Scheduler
  case class Delayed(duration: Duration, action: () => Unit) extends Scheduler
  case class Every(interval: Duration, action: () => Unit) extends Scheduler
  case class Run(action: () => Unit) extends Scheduler

  def apply(): Behavior[Scheduler] = withTimers { timers =>
    receive[Scheduler]((ctx, message) => {
      message match {
        case Delayed(duration, action) =>
          timers.startSingleTimer(Run(action), FiniteDuration(duration.toMillis, MILLISECONDS))
          same
        case Every(duration, action) =>
          timers.startTimerAtFixedRate(Run(action), FiniteDuration(duration.toMillis, MILLISECONDS))
          same
        case Run(action) =>
          try {
            action()
          } catch {
            case e: Throwable => ctx.log.error(e.getMessage, e)
          }
          same
      }
    })
  }
}