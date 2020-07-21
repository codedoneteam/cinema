package cinema.transaction.commit

import akka.actor.typed.Behavior
import cinema.message.Message

abstract class Commit[A](val behavior: Behavior[Message[A]])