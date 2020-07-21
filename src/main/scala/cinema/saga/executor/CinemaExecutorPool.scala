package cinema.saga.executor

import akka.actor.typed.scaladsl.{Behaviors, PoolRouter, Routers}
import akka.actor.typed.{ActorRef, SupervisorStrategy}
import cinema.orchestrator.SagaOrchestrator.OrchestratorTask
import cinema.saga.executor.ExecutorMessage.ExecutorMessage

object CinemaExecutorPool {
  def apply(orchestrator: ActorRef[OrchestratorTask[_]], pollSize: Int): PoolRouter[ExecutorMessage[_]] = {
    Routers.pool(poolSize = pollSize)(
      Behaviors.supervise(CinemaExecutor(orchestrator)).onFailure[Exception](SupervisorStrategy.resume))
  }
}