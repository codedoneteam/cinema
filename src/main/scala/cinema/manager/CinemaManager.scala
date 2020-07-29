package cinema.manager

import akka.actor.typed.DispatcherSelector._
import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors._
import cinema.SagaOrchestrator
import cinema.message.Payload
import cinema.orchestrator.SagaOrchestrator
import cinema.orchestrator.SagaOrchestrator.Start
import cinema.saga.context.SagaContext
import cinema.transaction.AbstractTransaction
import cinema.transaction.exception.NoSuchActorRefException

import scala.concurrent.Promise
import scala.reflect.runtime.universe.TypeTag
import scala.util.Try

object CinemaManager {

  sealed trait CinemaManagerTask[A]

  case class ProduceRef[A](behavior: Behavior[A],
                           name: String,
                           promise: Promise[ActorRef[A]],
                           typeTag: TypeTag[A],
                           ds: DispatcherSelector) extends CinemaManagerTask[A]


  case class StartSaga[A](tx: AbstractTransaction[A, _],
                          sagaContext: SagaContext[A],
                          dispatcherSelector: DispatcherSelector,
                          message: Payload[A]) extends CinemaManagerTask[A]

  case class Selection[A](tt: TypeTag[A], callback: Promise[ActorRef[A]]) extends CinemaManagerTask[A]



  def apply(sagaOrchestrator: Option[SagaOrchestrator] = None,
            actorRefs: Map[TypeTag[_], ActorRef[_]] = Map.empty,
            executorPollSize: Int): Behavior[CinemaManagerTask[_]] = receive[CinemaManagerTask[_]]((ctx, message) => {
    message match {
      case ProduceRef(behavior, name, promise, typeTag, ds) =>
        val actorRef = ctx.spawn(behavior = behavior, name = name, props = ds)
        promise.complete(Try(actorRef))
        apply(sagaOrchestrator = sagaOrchestrator,
          actorRefs = actorRefs + (typeTag -> actorRef),
          executorPollSize = executorPollSize)

      case StartSaga(tx, sagaContext, dispatcherSelector, msg) =>
        val ref = sagaOrchestrator.getOrElse(ctx.spawn(SagaOrchestrator(cinemaManager = ctx.self, executorPollSize = executorPollSize), "cinema-saga-orchestrator"))
        ref ! Start(tx, sagaContext, dispatcherSelector, msg)
        apply(sagaOrchestrator = Some(ref),
          actorRefs = actorRefs,
          executorPollSize = executorPollSize)

      case Selection(typeTag, promise) =>
        actorRefs.get(typeTag) match {
          case Some(actorRef) =>
            promise.complete(Try(actorRef.asInstanceOf[ActorRef[Any]]))
            same
          case _ => throw new NoSuchActorRefException(typeTag)
        }
    }
  })
}