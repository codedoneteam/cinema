package cinema.integration.first

import cinema.exception.ExpectedTestException
import cinema.integration.messages.FirstMessage.{FirstSecondMessage, Second}
import cinema.integration.messages.ZeroFirst.{First, ZeroFirstMessage}
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

object FirstTransaction extends Transaction[ZeroFirstMessage, FirstSecondMessage] {

  override def apply(implicit sc: SagaContext[ZeroFirstMessage]): Apply = execute { _ => {
    case First(i) => commit(Second(i))
    case x => throw new ExpectedTestException
   }
  }

  override def unapply(implicit sc: SagaContext[FirstSecondMessage]): UnApply = skip
}