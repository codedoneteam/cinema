package cinema.application.test

import cinema.application.test.OneTransactionMessage.{OneTransactionMessage, Process}
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction


object OneTransaction extends Transaction[OneTransactionMessage, String] {

  override def apply(implicit sc: SagaContext[OneTransactionMessage]): Apply = execute { _ => {
    case Process =>
      val data = sc.config.getString("app.data")
      commit(data)
    }
  }

  override def unapply(implicit sc: SagaContext[String]): UnApply = skip
}