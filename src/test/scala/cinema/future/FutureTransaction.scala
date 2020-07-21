package cinema.future

import cinema.exception.UnexpectedTestException
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.concurrent.{ExecutionContext, Future}

object FutureTransaction extends Transaction[Int, String] {
  override def apply(implicit sc: SagaContext[Int]): FutureTransaction.Apply = execute { _ => {
    i: Int =>
      implicit val ec: ExecutionContext = executionContext
      commitFuture(Future(i.toString))
    }
  }

  override def unapply(implicit sc: SagaContext[String]): FutureTransaction.UnApply = throw new UnexpectedTestException
}
