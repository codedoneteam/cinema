package cinema.timeout.transaction

import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.TestMessage.{TestMessage, UnitMessage}
import cinema.transaction.stateless.Transaction

object TimeoutTransaction extends Transaction[TestMessage, UnitMessage] {

  override def apply(implicit sc: SagaContext[TestMessage]): Apply = execute { _ => { _ =>
      commit(UnitMessage())
    }
  }

  override def unapply(implicit sc: SagaContext[UnitMessage]): UnApply = skip
}