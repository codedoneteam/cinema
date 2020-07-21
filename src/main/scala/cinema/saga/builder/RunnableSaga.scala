package cinema.saga.builder

import cinema.CinemaManager
import cinema.saga.Saga
import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}
import scala.reflect.runtime.universe.TypeTag

class RunnableSaga[In, Out](val saga: Saga[In, Out],
                            val cinemaManager: CinemaManager,
                            val duration: Duration,
                            val compensateDuration: Duration) {

  def run(message: In)(implicit config: Config, typeTag: TypeTag[In]): Future[Out] = {
    val promise = Promise[Out]()
    saga.run(cinemaManager = cinemaManager,
             duration = duration,
             compensateDuration = compensateDuration,
             message = message,
             config = config,
             promise = promise,
             closure = saga.closure)
    promise.future
  }

}