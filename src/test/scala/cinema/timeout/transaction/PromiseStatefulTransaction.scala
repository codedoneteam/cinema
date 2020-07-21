package cinema.timeout.transaction

import cinema.exception.UnexpectedTestException
import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.PromiseMessage.{InPromiseMessage, OutPromiseMessage}
import cinema.timeout.transaction.state.SomeState
import cinema.transaction.stateful.StatefulTransaction

import scala.util.Try

object PromiseStatefulTransaction extends StatefulTransaction[InPromiseMessage, SomeState, OutPromiseMessage] {
  override def apply(stateOpt: Option[SomeState])(implicit sc: SagaContext[InPromiseMessage]): Apply = execute { _ => _ => _ => {
    case InPromiseMessage(i, promise) if i == 2 =>
      Thread sleep 5000
      commit(OutPromiseMessage(i.toString, promise))
    case _ => throw new UnexpectedTestException
    }
  }

  override def unapply(stateOpt: Option[SomeState])(implicit sc: SagaContext[OutPromiseMessage]): UnApply = compensate { _ => _ => _ => {
    msg: OutPromiseMessage =>
      msg.promise.complete(Try(true))
      commit(InPromiseMessage(0, msg.promise))
    }
  }
}
