package cinema.transaction.behavior

import java.time.LocalDateTime

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors.stopped
import cinema.exception.CinemaException
import cinema.hvector.HVector.select
import cinema.message.{Message, Payload}
import cinema.saga.context.SagaContext
import cinema.saga.direction.{Forward, Reverse}
import cinema.saga.executor.ExecutorMessage.{ExecuteTransaction, SuccessComplete}
import cinema.transaction.commit.{FutureCommit, InstantCommit}
import cinema.transaction.exception.TransactionTimeoutException

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}

trait ApplyBehavior[In, Out] {
  this: ExecutionBehavior =>

    def commit(out: Out)(implicit sc: SagaContext[In], typeTag: TypeTag[In], typeTag2: TypeTag[Out]): InstantCommit[In] = {
      if (sc.executionExpired.isBefore(LocalDateTime.now())) InstantCommit(backward(out)) else InstantCommit(applyCommit(out))
    }

    def commitFuture(future: Future[Out])(implicit sc: SagaContext[In], typeTag: TypeTag[In], typeTag2: TypeTag[Out]): FutureCommit[In] = {
      implicit val ec: ExecutionContext = executionContext(sc)
      future onComplete {
        case Success(out) => commit(out)
        case Failure(e) => revert(e)
      }
      FutureCommit(stopped)
    }

    def rollback(out: Out)(implicit sc: SagaContext[In], typeTag: TypeTag[In], typeTag2: TypeTag[Out]): InstantCommit[In] = {
      InstantCommit(backward(out))
    }

    def checkTimeout(implicit sc: SagaContext[In]): Unit = {
      if (sc.executionExpired.isBefore(LocalDateTime.now())) throw new TransactionTimeoutException
    }

    protected def revert[A](exception: Throwable)(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
      select[Reverse[A]](sc.reverse) match  {
        case Some(reverse) =>
          val behavior = reverse.tx.unapply(sc.turn())
          val task = ExecuteTransaction[A](behavior = behavior,
            dispatcherSelector = reverse.ds,
            message = Payload(sc.in))
          sc.executor ! task
        case _ =>
          sc.promise.failure(exception)
      }
      throw exception
    }

    private def backward(out: Out)(implicit sc: SagaContext[In], typeTag: TypeTag[In], typeTag2: TypeTag[Out]): Behavior[Message[In]] = {
      select[Reverse[Out]](sc.reverse) match {
        case Some(forward) =>
          val behavior = forward.tx.unapply(sc.shift(out).turn())
          val task = ExecuteTransaction(behavior = behavior,
            dispatcherSelector = forward.ds,
            message = Payload[Out](out))
          sc.executor ! task
        case _ => throw new CinemaException
      }
      stopped[Message[In]]
    }

    private def applyCommit(out: Out)(implicit sc: SagaContext[In], typeTag: TypeTag[In], typeTag2: TypeTag[Out]): Behavior[Message[In]] = {
      select[Forward[Out]](sc.forward) match {
        case Some(selector) =>
          val behavior: Behavior[Message[Out]] = selector.tx.apply(sc.shift(out))
          val task = ExecuteTransaction(behavior = behavior,
            dispatcherSelector = selector.ds,
            message = Payload[Out](out))
          sc.executor ! task
        case _ =>
          if (sc.executor != null) sc.executor ! SuccessComplete(sc.id)
          sc.promise.asInstanceOf[Promise[Out]].complete(Try(out))
      }
      stopped[Message[In]]
    }
}
