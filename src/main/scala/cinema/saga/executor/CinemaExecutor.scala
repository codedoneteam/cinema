package cinema.saga.executor

import java.util.UUID

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors._
import cinema.orchestrator.SagaOrchestrator._
import cinema.saga.executor.ExecutorMessage.{ActorSelectionExecution, ExecuteTransaction, ExecutorMessage, RollbackSaga, SagaConsistencyException, StartSagaExecution, SuccessComplete}

import scala.util.{Failure, Try}

object CinemaExecutor {
  def apply(orchestrator: ActorRef[OrchestratorTask[_]]): Receive[ExecutorMessage[_]] = receive[ExecutorMessage[_]]((ctx, message) => {
    message match {
      case StartSagaExecution(tx, context, dispatcherSelector, msg) =>
        val behavior = tx.apply(context.copy(
          system = Some(ctx.system),
          dispatcherSelector = dispatcherSelector))
        ctx.self ! ExecuteTransaction(behavior, dispatcherSelector, msg)
        same
      case ExecuteTransaction(behavior, dispatcherSelector, msg) => Try {
        val ref = ctx.spawn(behavior = behavior,
          name = UUID.randomUUID().toString,
          props = dispatcherSelector)
        ref ! msg
        } match {
          case Failure(e) =>
            ctx.log.error(e.getMessage)
            same
          case _ => same
        }
      case SuccessComplete(id) =>
        orchestrator ! Completed(id)
        same
      case RollbackSaga(id) =>
        orchestrator ! Rollback(id)
        same
      case SagaConsistencyException(id) =>
        orchestrator ! Inconsistent(id)
        same
      case ActorSelectionExecution(typeTag, promise) =>
        orchestrator ! ActorSelection(typeTag, promise)
        same
    }
  })
}