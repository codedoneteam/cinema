package cinema.suspend

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors._
import cinema.message.{Message, Payload}
import cinema.suspend.InMessage.{CalcResult, InMessage}

object Calc {

  sealed trait Calc
  case class Inc(i: Int, replayTo: ActorRef[Message[InMessage]]) extends Calc

  def apply(): Receive[Calc] = receive[Calc] { (ctx, message) =>
    message match {
      case Inc(i, replayTo) =>
        replayTo ! Payload(CalcResult(i + 1))
        same
    }
  }

}