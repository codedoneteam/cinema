package cinema.application.test

import cinema.application.test.OneTransactionMessage.{OneTransactionMessage, Process}
import cinema.exception.UnexpectedTestException
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction


object OneTransaction extends Transaction[OneTransactionMessage, String] {

  case class OneTransactionConfig(data: String = "TEST")

  override def apply(implicit sc: SagaContext[OneTransactionMessage]): Apply = execute { _ => {
    case Process(42) =>
      config { cfg: OneTransactionConfig =>
        commit(cfg.data)
      }
    case _ => throw new UnexpectedTestException
    }
  }

  override def unapply(implicit sc: SagaContext[String]): UnApply = skip
}