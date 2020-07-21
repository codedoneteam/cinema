package cinema.saga


import cinema.saga.FailureMessages.{InMessage, OutMessage}
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.language.postfixOps

object SomeFailureTransaction extends Transaction[InMessage, OutMessage] {
  override def apply(implicit sc: SagaContext[InMessage]): Apply = execute { _ => {
      case InMessage(i) => rollback(OutMessage(i + 1))
    }
  }

  override def unapply(implicit sc: SagaContext[OutMessage]): UnApply = compensate { _ => {
      case OutMessage(i) => commit(InMessage(i - 1))
    }
  }
}