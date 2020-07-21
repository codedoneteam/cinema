package cinema.timeout.transaction

import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.PromiseMessage.{InPromiseMessage, OutPromiseMessage}
import cinema.transaction.stateless.Transaction

import scala.util.Try

object PromiseTransaction extends Transaction[InPromiseMessage, OutPromiseMessage] {
  override def apply(implicit sc: SagaContext[InPromiseMessage]): Apply = execute { _ => {
    case InPromiseMessage(i, promise) if i == -1 =>
      Thread sleep 1500
      checkTimeout
      commit(OutPromiseMessage(i.toString, promise))
    case InPromiseMessage(i, promise) if i == 0 =>
      Thread sleep 2000
      commit(OutPromiseMessage(i.toString, promise))
    case InPromiseMessage(i, promise) =>
      Thread sleep 5000
      commit(OutPromiseMessage(i.toString, promise))
    }
  }

  override def unapply(implicit sc: SagaContext[OutPromiseMessage]): UnApply = compensate { _ => {
    msg: OutPromiseMessage =>
      msg.promise.complete(Try(true))
      commit(InPromiseMessage(0, msg.promise))
    }
  }
}
