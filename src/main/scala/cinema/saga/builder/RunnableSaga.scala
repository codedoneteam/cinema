package cinema.saga.builder

import cinema.CinemaManager
import cinema.config.ConfigBox
import cinema.saga.Saga

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}
import scala.reflect.runtime.universe.TypeTag

class RunnableSaga[In, Out](saga: Saga[In, Out],
                            cinemaManager: CinemaManager,
                            duration: Duration,
                            compensateDuration: Duration) {

  def run(message: In)(implicit configInclude: ConfigBox, typeTag: TypeTag[In]): Future[Out] = {
    val promise = Promise[Out]()
    saga.run(cinemaManager = cinemaManager,
             duration = duration,
             compensateDuration = compensateDuration,
             message = message,
             config = configInclude.config,
             promise = promise,
             closure = saga.closure)
    promise.future
  }

}

object RunnableSaga {
  def apply[In, Out](saga: Saga[In, Out],
            cinemaManager: CinemaManager,
            duration: Duration,
            compensateDuration: Duration): RunnableSaga[In, Out] = new RunnableSaga(saga, cinemaManager, duration, compensateDuration)

}