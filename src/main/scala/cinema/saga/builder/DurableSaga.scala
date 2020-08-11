package cinema.saga.builder

import cinema.CinemaManager
import cinema.saga.Saga

import scala.concurrent.duration.Duration

class DurableSaga[In, Out](saga: Saga[In, Out], cinemaManager: CinemaManager) {

  def duration(duration: Duration): RunnableSaga[In, Out] = {
    RunnableSaga(saga = saga, cinemaManager = cinemaManager, duration = duration, compensateDuration = duration)
  }

  def duration(duration: Duration, compensateDuration: Duration): RunnableSaga[In, Out] = {
    RunnableSaga(saga = saga, cinemaManager = cinemaManager, duration = duration, compensateDuration = compensateDuration)
  }
}

object DurableSaga {
  def apply[In, Out](saga: Saga[In, Out], cinemaManager: CinemaManager): DurableSaga[In, Out] = {
    new DurableSaga(saga, cinemaManager)
  }
}