package cinema.scheduler

import cinema.app.ActorAware
import cinema.scheduler.Scheduler.{Delayed, Every}

import scala.concurrent.duration.Duration

trait SchedulerAware {
  this: ActorAware =>
  def after(duration: Duration)(action: => Unit): Unit = {
    actorOf(Scheduler()) { ref =>
      ref ! Delayed(duration, () => action)
    }
  }

  def every(duration: Duration)(action: => Unit): Unit = {
    actorOf(Scheduler()) { ref =>
      ref ! Every(duration, () => action)
    }
  }
}