package cinema.after

import cinema.after.After.Delayed
import cinema.app.ActorAware

import scala.concurrent.duration.Duration

trait AfterAware {
  this: ActorAware =>
    def after(duration: Duration)(action: => Unit): Unit = {
      actorOf(After()) { ref =>
        ref ! Delayed(duration, () => action)
      }
    }
}