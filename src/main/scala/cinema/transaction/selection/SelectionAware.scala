package cinema.transaction.selection

import akka.actor.typed.{ActorRef, Behavior}
import cinema.saga.context.SagaContext
import cinema.saga.executor.ExecutorMessage.ActorSelectionExecution
import cinema.transaction.behavior.ExecutionBehavior

import scala.concurrent.{ExecutionContext, Promise}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success}

trait SelectionAware {
  this: ExecutionBehavior =>
    def actorSelection[A](callback: ActorRef[A] => Unit)(implicit sc: SagaContext[_], typeTag: TypeTag[A]): Unit = {
      val promise = Promise[ActorRef[A]]()
      implicit val ec: ExecutionContext = executionContext(sc)
      sc.executor ! ActorSelectionExecution(typeTag, promise)
      promise.future.onComplete {
        case Success(ref) => callback(ref)
        case Failure(e) => throw e
      }
    }
}