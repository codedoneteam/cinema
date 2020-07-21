package cinema.rollback.transaction

import cinema.exception.ExpectedTestException
import cinema.rollback.transaction.TestActorMessage.TestActorMessage
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction


object RollbackTransaction extends Transaction[TestActorMessage, Boolean] {

  override def apply(implicit sc: SagaContext[TestActorMessage]): Apply = execute { _ =>_ =>
    throw new ExpectedTestException
  }

  override def unapply(implicit sc: SagaContext[Boolean]): UnApply =  skip
}