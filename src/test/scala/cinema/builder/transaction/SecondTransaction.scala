package cinema.builder.transaction

import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.language.postfixOps

object SecondTransaction extends Transaction[String, Int] {
  override def apply(implicit sc: SagaContext[String]): Apply = execute { _ => x => commit(1)
  }

  override def unapply(implicit sc: SagaContext[Int]): UnApply = skip
}