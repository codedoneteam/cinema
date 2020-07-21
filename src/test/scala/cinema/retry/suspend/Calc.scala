package cinema.retry.suspend

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors._
import cinema.message.{Message, Payload}
import cinema.retry.suspend.RetryMessage.{CalcMessage, RetryMessage}

object Calc {

  sealed trait Calc
  case class Inc(i: Int, replayTo: ActorRef[Message[RetryMessage]]) extends Calc

  def apply(): Receive[Calc] = receive[Calc] { (ctx, message) =>
    message match {
      case Inc(i, replayTo) =>
        replayTo ! Payload(CalcMessage(i + 1))
        same
    }
  }

}