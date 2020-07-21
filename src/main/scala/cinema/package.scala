import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.ActorContext
import cinema.manager.CinemaManager.CinemaManagerTask
import cinema.message.Message
import cinema.orchestrator.SagaOrchestrator.OrchestratorTask
import cinema.saga.executor.ExecutorMessage.ExecutorMessage

import scala.concurrent.duration.Duration

package object cinema {
  type OptionRef[A] = Option[ActorRef[Message[A]]]

  type TransactionRef[A] =  ActorRef[Message[A]]

  type TaskContext = ActorContext[ExecutorMessage[_]]

  type TaskExecutor = ActorRef[ExecutorMessage[_]]

  type CinemaManager = ActorRef[CinemaManagerTask[_]]

  type SagaOrchestrator = ActorRef[OrchestratorTask[_]]

  type RetryStrategy = Throwable => Duration
}
