package cinema.stateful

import cinema.log.Log
import cinema.saga.context.SagaContext
import cinema.stateful.TestMessage.{First, Second, TestTransactionMessage}
import cinema.stateful.TestOutMessage.{TestOut, TestOutMessage}
import cinema.transaction.stateful.StatefulTransaction

import scala.concurrent.duration.{FiniteDuration, SECONDS}


object SomeStatefulTransaction extends StatefulTransaction[TestTransactionMessage, SomeState, TestOutMessage] {

  override def apply(state: Option[SomeState])(implicit sc: SagaContext[TestTransactionMessage]): Apply = execute {timers: Timers => _ =>
    log: Log => {
      case First =>
        timers.startSingleTimer(Second, FiniteDuration(1, SECONDS))
        await
      case Second =>
        commit(TestOut)
    }
  }

  override def unapply(state: Option[SomeState])(implicit sc: SagaContext[TestOutMessage]): UnApply = skip
}