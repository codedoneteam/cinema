package cinema.transaction.behavior

import java.time.LocalDateTime

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors.stopped
import cinema.hvector.HVector.select
import cinema.message.{Message, Payload}
import cinema.saga.context.SagaContext
import cinema.saga.direction.Reverse
import cinema.saga.executor.ExecutorMessage.{ExecuteTransaction, RollbackSaga, SagaConsistencyException}
import cinema.transaction.commit.{FutureCommit, InstantCommit}
import cinema.transaction.exception.{ConsistencyException, SagaException, TransactionException}

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success}

trait UnApplyBehavior[In, Out] {
  this: ExecutionBehavior =>

    protected def commit(in: In)(implicit sc: SagaContext[Out], typeTag: TypeTag[In]): InstantCommit[Out] = {
      if (sc.executionExpired.isBefore(LocalDateTime.now())) {
        sc.executor ! SagaConsistencyException(sc.id)
        throw new ConsistencyException(sc.id)
      } else {
        select[Reverse[In]](sc.reverse) match {
          case Some(selector) =>
            val reverse = select[Reverse[In]](sc.reverse).get
            val behavior = selector.tx.unapply(sc.shift(in))
            val task = ExecuteTransaction(behavior = behavior,
              dispatcherSelector = reverse.ds,
              message = Payload[In](in))
            sc.executor ! task
          case _ =>
            sc.executor ! RollbackSaga(sc.id)
            sc.promise.failure(new SagaException(in))
        }
      }
      InstantCommit(stopped[Message[Out]])
    }

  protected def commitFuture(future: Future[In])(implicit sc: SagaContext[Out], typeTag: TypeTag[In]): FutureCommit[Out] = {
    implicit val ec: ExecutionContext = executionContext(sc)
    future onComplete {
      case Success(in) =>
        commit(in)
      case Failure(e) =>
        sc.executor ! SagaConsistencyException(sc.id)
        throw new ConsistencyException(sc.id)
    }
    FutureCommit(stopped)
  }

  protected def skip(implicit sc: SagaContext[Out], typeTag: TypeTag[In]): Behavior[Message[Out]] = {
    select[In](sc.messages) match {
      case Some(in) =>
        commit(in)(sc, typeTag).behavior
      case _ =>
        sc.executor ! RollbackSaga(sc.id)
        sc.promise.failure(new TransactionException)
        stopped[Message[Out]]
    }
  }
}
