package cinema.transaction.commit

import akka.actor.typed.Behavior
import cinema.message.Message

case class InstantCommit[A](override val behavior: Behavior[Message[A]]) extends Commit(behavior)