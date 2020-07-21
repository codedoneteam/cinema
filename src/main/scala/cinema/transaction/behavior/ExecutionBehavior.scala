package cinema.transaction.behavior

import cinema.exception.CinemaException
import cinema.saga.context.SagaContext

import scala.concurrent.ExecutionContext

trait ExecutionBehavior {
  def executionContext[A](implicit sc: SagaContext[A]): ExecutionContext = {
    sc.system match {
      case Some(system) => system.dispatchers.lookup(sc.dispatcherSelector)
      case _ => throw new CinemaException
    }
  }
}
