package cinema.suspend.transaction

import akka.actor.typed.ActorRef
import cinema.log.Log
import cinema.message.Message
import cinema.saga.context.SagaContext
import cinema.suspend.actor.Calc.{Calc, Inc}
import cinema.suspend.transaction.InMessage.{CalcResult, InMessage, One}
import cinema.suspend.transaction.OutMessage.Two
import cinema.transaction.suspend.SuspendTransaction


object SelectionAwareTransaction extends SuspendTransaction[InMessage, Two] {

  override def apply(implicit sc: SagaContext[InMessage]): Apply = execute { self: Self => log: Log => {
    case One(i) =>
      actorSelection[Calc]{ actorRef =>
        actorRef ! Inc(i, self.asInstanceOf[ActorRef[Message[Any]]])
      }
      await
    case CalcResult(i)   =>
      commit(Two(i))
    }
  }

  override def unapply(implicit sc: SagaContext[Two]): UnApply = skip
}