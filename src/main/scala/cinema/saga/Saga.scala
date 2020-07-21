package cinema.saga

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.typed.DispatcherSelector
import akka.actor.typed.DispatcherSelector._
import cinema.CinemaManager
import cinema.exception.CinemaException
import cinema.hvector.HVector.{HNil, HVector, select}
import cinema.manager.CinemaManager.StartSaga
import cinema.message.Payload
import cinema.saga.builder.SagaDuration._
import cinema.saga.context.SagaContext
import cinema.saga.direction.{Forward, Reverse}
import cinema.transaction.AbstractTransaction
import com.typesafe.config.Config

import scala.concurrent.Promise
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe.TypeTag


case class Saga[In, Out](id: UUID = UUID.randomUUID(),
                         cinemaManager: CinemaManager,
                         forward: HVector,
                         reverse: HVector,
                         promise: Promise[Out] = Promise[Out](),
                         closure: HVector = HNil) {

  def run(cinemaManager: CinemaManager,
          duration: Duration,
          compensateDuration: Duration,
          message: In,
          config: Config,
          promise: Promise[Out],
          closure: HVector)(implicit inTag: TypeTag[In]): Unit = {
    val selector = select[Forward[In]](forward).getOrElse(throw new CinemaException)

    val context = SagaContext[In](in = message,
      id = id,
      system = None,
      dispatcherSelector = defaultDispatcher(),
      executionExpired = LocalDateTime.now() + duration,
      compensateDuration = compensateDuration,
      duration = duration,
      executor = null,
      config = config,
      forward = forward,
      reverse = reverse,
      promise = promise,
      messages = HNil,
      closure = closure)

    val task = StartSaga(tx = selector.tx,
                                       sagaContext = context,
                                       dispatcherSelector = selector.ds,
                                       message = Payload(message))

    cinemaManager ! task
  }
}

object Saga {
  def apply[In : TypeTag, Out](txs: AbstractTransaction[In, _])(cinemaManager: CinemaManager): Saga[In, Out] = {
    apply(cinemaManager, txs -> defaultDispatcher())
  }

  def apply[In, Out](cinemaManager: CinemaManager, arrow: (AbstractTransaction[In, _], DispatcherSelector))(implicit typeTagIn: TypeTag[In]): Saga[In, Out] = {
    val (tx, ds) = arrow
    new Saga[In, Out](cinemaManager = cinemaManager,
      forward = Forward(tx, ds) :: HNil,
      reverse = Reverse(tx, ds) :: HNil)
  }
}