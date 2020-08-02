package cinema.builder.transaction

import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.language.postfixOps

object FirstTransaction extends Transaction[Int, String] {
  override def apply(implicit sc: SagaContext[Int]): Apply = execute { _ => x => commit(x.toString)
  }

  override def unapply(implicit sc: SagaContext[String]): UnApply = skip
}