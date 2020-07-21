package cinema.transaction.closure

import cinema.hvector.HVector._
import cinema.saga.context.SagaContext
import cinema.transaction.commit.Commit
import cinema.transaction.exception.ClosureException

import scala.reflect.runtime.universe.TypeTag

trait ClosureAware[In] {
  def closure[Closure](f: Closure => Commit[In])(implicit sc: SagaContext[In], typeTag: TypeTag[Closure]): Commit[In] = {
    select[Closure](sc.closure) match {
      case Some(closure) => f(closure)
      case _ => throw new ClosureException
    }
  }
}