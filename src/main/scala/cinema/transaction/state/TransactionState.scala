package cinema.transaction.state

import cinema.saga.context.SagaContext
import cinema.transaction.AbstractTransaction


trait TransactionState[S, In, Out] {
  this: AbstractTransaction[In, Out] =>
    def apply(state: Option[S])(implicit sc: SagaContext[In]): Apply

    def apply(implicit sc: SagaContext[In]): Apply = apply(None)(sc)

    def unapply(implicit sc: SagaContext[Out]): UnApply = unapply(None)(sc)

    def unapply(state: Option[S])(implicit sc: SagaContext[Out]): UnApply
}