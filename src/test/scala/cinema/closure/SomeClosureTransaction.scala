package cinema.closure


import cinema.closure.ClosureMessages.{InMessage, OutMessage}
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.language.postfixOps

object SomeClosureTransaction extends Transaction[InMessage, OutMessage] {
  override def apply(implicit sc: SagaContext[InMessage]): Apply = execute { _ => {
      case InMessage() =>
        closure { i: Int =>
          commit(OutMessage(i))
        }
    }
  }

  override def unapply(implicit sc: SagaContext[OutMessage]): UnApply = skip
}