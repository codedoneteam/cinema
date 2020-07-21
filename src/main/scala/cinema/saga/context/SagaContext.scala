package cinema.saga.context

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import cinema.TaskExecutor
import cinema.hvector.HVector._
import cinema.saga.builder.SagaDuration._
import com.typesafe.config.Config

import scala.concurrent.Promise
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe.TypeTag


case class SagaContext[A](in: A,
                          id: UUID,
                          system: Option[ActorSystem[_]],
                          dispatcherSelector: DispatcherSelector,
                          executionExpired: LocalDateTime,
                          duration: Duration,
                          compensateDuration: Duration,
                          executor: TaskExecutor,
                          config: Config,
                          forward: HVector,
                          reverse: HVector,
                          promise: Promise[_],
                          messages: HVector,
                          closure: HVector) {

  def shift[B : TypeTag](b: B): SagaContext[B] = this.copy(in = b, messages = b :: messages)

  def turn(): SagaContext[A] = this.copy(executionExpired = LocalDateTime.now() + compensateDuration)

  def changePromise[B](promise: Promise[B]): SagaContext[A] = this.copy(promise = promise)
}