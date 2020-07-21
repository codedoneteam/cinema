package cinema.timeout.transaction

import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.PromiseMessage.OutPromiseMessage
import cinema.timeout.transaction.message.TestMessage.TestMessage
import cinema.timeout.transaction.state.SomeState
import cinema.transaction.stateful.StatefulTransaction

import scala.concurrent.duration.{FiniteDuration, SECONDS}

object CheckedTimeoutTransaction extends StatefulTransaction[TestMessage, SomeState, OutPromiseMessage] {

  override def apply(state: Option[SomeState])(implicit sc: SagaContext[TestMessage]): CheckedTimeoutTransaction.Apply = execute { timers: Timers => _  =>_ => {
    msg: TestMessage =>
      timers.startSingleTimer(msg, FiniteDuration(1, SECONDS))
      apply(sc)
    }
  }

  override def unapply(state: Option[SomeState])(implicit sc: SagaContext[OutPromiseMessage]): CheckedTimeoutTransaction.UnApply = skip
}