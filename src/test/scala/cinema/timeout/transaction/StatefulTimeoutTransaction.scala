package cinema.timeout.transaction

import cinema.log.Log
import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.PromiseMessage.OutPromiseMessage
import cinema.timeout.transaction.message.TestMessage.TestMessage
import cinema.timeout.transaction.state.SomeState
import cinema.transaction.stateful.StatefulTransaction

import scala.concurrent.duration.{FiniteDuration, _}

object StatefulTimeoutTransaction extends StatefulTransaction[TestMessage, SomeState, OutPromiseMessage] {
  override def apply(state: Option[SomeState])(implicit sc: SagaContext[TestMessage]): Apply = execute { timers: Timers => _ =>log: Log =>msg =>
      state match {
        case Some(s) =>
          log.infoBlock(s.i) {
            timers.startSingleTimer(msg, FiniteDuration(1, SECONDS))
            apply(Some(s.copy(s.i + 1)))
          }
        case _ =>
          timers.startSingleTimer(msg, FiniteDuration(1, SECONDS))
          apply(Some(SomeState(0)))
    }
  }

  override def unapply(state: Option[SomeState])(implicit sc: SagaContext[OutPromiseMessage]): UnApply = skip
}