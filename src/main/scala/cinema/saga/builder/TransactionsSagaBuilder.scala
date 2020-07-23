package cinema.saga.builder

import akka.actor.typed.DispatcherSelector
import akka.actor.typed.DispatcherSelector._
import cinema.CinemaManager
import cinema.hvector.HVector.HNil
import cinema.saga.Saga
import cinema.saga.direction.{Forward, Reverse}
import cinema.transaction.AbstractTransaction

import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe.TypeTag


class TransactionsSagaBuilder[In, Out](saga: Saga[In, Out], cinemaManager: CinemaManager) {
  def transaction[In2 <: Out, Out2](tx: AbstractTransaction[In2, Out2], ds: DispatcherSelector = defaultDispatcher())
                                   (implicit in2: TypeTag[In2], out2: TypeTag[Out2]): TransactionsSagaBuilder[In, Out2] = {
    val forward = Forward(tx, ds) :: HNil
    val reverse = Reverse(tx, ds) :: HNil
    new TransactionsSagaBuilder[In, Out2](new Saga[In, Out2](cinemaManager = saga.cinemaManager,
                                                              forward = saga.forward ++ forward,
                                                              reverse = reverse ++ saga.reverse), cinemaManager)
  }

  def build(): DurableSaga[In, Out] = {
    DurableSaga(saga = saga, cinemaManager = cinemaManager)
  }
}

object TransactionsSagaBuilder {
  def apply[In, Out](saga: Saga[In, Out], cinemaManager: CinemaManager): TransactionsSagaBuilder[In, Out] = {
    new TransactionsSagaBuilder(saga, cinemaManager)
  }
}