package cinema.orchestrator

import java.util.UUID

import akka.actor.typed.scaladsl.Behaviors._
import akka.actor.typed.{ActorRef, Behavior, DispatcherSelector}
import cinema.TaskExecutor
import cinema.manager.CinemaManager.{CinemaManagerTask, Selection}
import cinema.message.Payload
import cinema.saga.context.SagaContext
import cinema.saga.executor.CinemaExecutorPool
import cinema.saga.executor.ExecutorMessage.StartSagaExecution
import cinema.transaction.AbstractTransaction

import scala.concurrent.Promise
import scala.reflect.runtime.universe.TypeTag

object SagaOrchestrator {

  sealed trait OrchestratorTask[A]

  case class Start[A](tx: AbstractTransaction[A, _],
                      sagaContext: SagaContext[A],
                      dispatcherSelector: DispatcherSelector,
                      message: Payload[A]) extends OrchestratorTask[A]

  case class Completed[A](id: UUID) extends OrchestratorTask[A]

  case class Rollback[A](id: UUID) extends OrchestratorTask[A]

  case class Inconsistent[A](id: UUID) extends OrchestratorTask[A]

  case class ActorSelection[A](typeTag: TypeTag[A], behavior: () => Behavior[A], callback: Promise[ActorRef[A]]) extends OrchestratorTask[A]


  def apply(cinemaManager: ActorRef[CinemaManagerTask[_]], executor: Option[TaskExecutor] = None, executorPollSize: Int): Receive[OrchestratorTask[_]] = {
    receive[OrchestratorTask[_]]((ctx, message) => {
      message match {
        case Start(tx, sagaContext, dispatcherSelector, msg) =>
          val ref = executor.getOrElse(ctx.spawn(CinemaExecutorPool(ctx.self, executorPollSize), "cinema-executor", DispatcherSelector.fromConfig("cinema.saga-executor-dispatcher")))
          ref ! StartSagaExecution(tx, sagaContext.copy(executor = ref), dispatcherSelector, msg)
          apply(cinemaManager, Some(ref), executorPollSize)
        case Completed(id) =>
          ctx.log.debug(s"Saga $id completed")
          same
        case Rollback(id) =>
          ctx.log.debug(s"Saga $id failed")
          same
        case Inconsistent(id) =>
          ctx.log.debug(s"Saga $id completed")
          same
        case ActorSelection(typeTag, behavior, promise) =>
          cinemaManager ! Selection(typeTag, behavior, promise)
          same
      }
    })
  }

}
