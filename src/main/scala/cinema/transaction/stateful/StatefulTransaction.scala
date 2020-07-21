package cinema.transaction.stateful

import akka.actor.typed.scaladsl.TimerScheduler
import akka.actor.typed.{ActorRef, Behavior}
import cinema.log.Log
import cinema.message.Message
import cinema.saga.context.SagaContext
import cinema.saga.executor.ExecutorMessage.SagaConsistencyException
import cinema.transaction.AbstractTransaction
import cinema.transaction.commit.{Commit, SuspendAware}
import cinema.transaction.exception.ConsistencyException
import cinema.transaction.state.TransactionState

import scala.reflect.runtime.universe.TypeTag

trait StatefulTransaction[In, State, Out] extends AbstractTransaction[In, Out] with TransactionState[State, In, Out] with SuspendAware {

  def execute[A <: In](f: TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    tx(f){ e => timers => {
        timers.cancelAll()
        revert(e)
      }
    }
  }

  def compensate[A <: Out](f:  TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    tx(f){ _ => timers =>
      timers.cancelAll()
      sc.executor ! SagaConsistencyException(sc.id)
      throw new ConsistencyException(sc.id)
    }
  }
}