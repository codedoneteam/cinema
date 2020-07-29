package cinema.stateless.transaction

import cinema.saga.context.SagaContext
import cinema.stateless.transaction.StatelessMessages.{InMessage, OutMessage}
import cinema.transaction.stateless.Transaction

import scala.language.postfixOps

object SomeStatelessTransaction extends Transaction[InMessage, OutMessage] {
  override def apply(implicit sc: SagaContext[InMessage]): Apply = execute { _ => {
      case InMessage() => commit(OutMessage())
    }
  }

  override def unapply(implicit sc: SagaContext[OutMessage]): UnApply = skip
}