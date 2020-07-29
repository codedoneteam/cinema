package cinema.scheduler

import akka.actor.typed.DispatcherSelector._
import cinema.app.ActorAware
import cinema.scheduler.Scheduler.{Delayed, Every}

import scala.concurrent.duration.Duration

trait SchedulerAware {
  this: ActorAware =>
  def after(duration: Duration)(action: => Unit): Unit = {
    actorOf(behavior = Scheduler(), ds = fromConfig("cinema.scheduler-dispatcher")) { ref =>
      ref ! Delayed(duration, () => action)
    }
  }

  def every(duration: Duration)(action: => Unit): Unit = {
    actorOf(behavior = Scheduler(), ds = fromConfig("cinema.scheduler-dispatcher")) { ref =>
      ref ! Every(duration, () => action)
    }
  }
}