package cinema.transaction

import cinema.saga.context.SagaContext
import cinema.transaction.behavior.TransactionBehavior
import cinema.transaction.closure.ClosureAware
import cinema.transaction.selection.SelectionAware
import cinema.transaction.types.TransactionTypes

trait AbstractTransaction[In, Out] extends TransactionBehavior[In, Out]
  with TransactionTypes[In, Out]
  with ClosureAware[In]
  with ImplicitTransaction[In]
  with SelectionAware {

  def apply(implicit sc: SagaContext[In]): Apply

  def unapply(implicit sc: SagaContext[Out]): UnApply
}