package cinema.transaction.commit

import akka.actor.typed.Behavior
import cinema.message.Message

case class FutureCommit[A](override val behavior: Behavior[Message[A]]) extends Commit(behavior)
