package cinema.saga.executor

import java.util.UUID

import akka.actor.typed.{ActorRef, Behavior, DispatcherSelector}
import cinema.message.{Message, Payload}
import cinema.saga.context.SagaContext
import cinema.transaction.AbstractTransaction

import scala.concurrent.Promise
import scala.reflect.runtime.universe.TypeTag

object ExecutorMessage {
  sealed trait ExecutorMessage[A]

  case class StartSagaExecution[A](tx: AbstractTransaction[A, _],
                                   context: SagaContext[A],
                                   dispatcherSelector: DispatcherSelector,
                                   message: Payload[A]) extends ExecutorMessage[A]

  case class ExecuteTransaction[A](behavior: Behavior[Message[A]],
                                   dispatcherSelector: DispatcherSelector,
                                   message: Payload[A]) extends ExecutorMessage[A]

  case class SuccessComplete[A](id: UUID) extends ExecutorMessage[A]

  case class RollbackSaga[A](id: UUID) extends ExecutorMessage[A]

  case class SagaConsistencyException[A](id: UUID) extends ExecutorMessage[A]

  case class ActorSelectionExecution[A](tt: TypeTag[A], callback: Promise[ActorRef[A]]) extends ExecutorMessage[A]
}