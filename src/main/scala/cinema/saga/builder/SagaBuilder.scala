package cinema.saga.builder

import akka.actor.typed.DispatcherSelector
import akka.actor.typed.DispatcherSelector._
import cinema.CinemaManager
import cinema.hvector.HVector.{HNil, HVector}
import cinema.saga.Saga
import cinema.saga.direction.{Forward, Reverse}
import cinema.transaction.AbstractTransaction

import scala.reflect.runtime.universe.TypeTag


class SagaBuilder(cinemaManager: CinemaManager, closure: HVector) {
  def transaction[In, Out](tx: AbstractTransaction[In, Out],
                           ds: DispatcherSelector = defaultDispatcher())
                          (implicit in: TypeTag[In], out: TypeTag[Out]): TransactionsSagaBuilder[In, Out] = {
    val forward = Forward(tx, ds) :: HNil
    val reverse = Reverse(tx, ds) :: HNil
    TransactionsSagaBuilder[In, Out](new Saga[In, Out](cinemaManager = cinemaManager,
                                                           forward = forward,
                                                           reverse = reverse,
                                                           closure = closure), cinemaManager = cinemaManager)
  }
}

object SagaBuilder {
  def apply()(implicit cinemaManager: CinemaManager): SagaBuilder = new SagaBuilder(cinemaManager, HNil)

  def apply[A, Closure](c: Closure)(implicit cinemaManager: CinemaManager, typeTag: TypeTag[Closure]): SagaBuilder = {
    new SagaBuilder(cinemaManager, c :: HNil)
  }
}