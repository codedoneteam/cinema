package cinema.transaction.commit

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors._
import cinema.message.Message

trait SuspendAware {
  implicit class SuspendCommit[A](override val behavior: Behavior[Message[A]]) extends Commit(behavior)

  def await[A] = new SuspendCommit[A](same)
}