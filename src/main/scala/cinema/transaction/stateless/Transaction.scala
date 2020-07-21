package cinema.transaction.stateless

import java.time.LocalDateTime

import akka.actor.typed.scaladsl.Behaviors._
import akka.actor.typed.scaladsl.TimerScheduler
import akka.actor.typed.{ActorRef, Behavior}
import cinema.log.Log
import cinema.message.{Message, Payload}
import cinema.saga.context.SagaContext
import cinema.saga.executor.ExecutorMessage.SagaConsistencyException
import cinema.transaction.AbstractTransaction
import cinema.transaction.commit.{Commit, InstantCommit}
import cinema.transaction.exception.ConsistencyException

import scala.concurrent.duration.{Duration, FiniteDuration, _}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}

trait Transaction[In, Out] extends AbstractTransaction[In, Out] {

  def execute[A <: In](f: Log => A => Commit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    val f2: TimerScheduler[Message[A]]  => ActorRef[Message[A]] => Log => A => Commit[A] = _ => _ => f
    tx(f2){ e => timers => {
      timers.cancelAll()
      revert(e)
      }
    }
  }

  def execute[A <: In](retry: Throwable => Duration)(f: Log => A => InstantCommit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    val f2: TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A] = timers => _ => log => a => Try {
      f(log)(a)
    } match {
      case Success(v) => v
      case Failure(e) =>
        log.error(e.getMessage, e)
        Try {
          timers.startSingleTimer(Payload(a), FiniteDuration(retry(e).toMillis, MILLISECONDS))
        } match {
          case Success(_) =>
            InstantCommit(same)
          case Failure(ex) =>
            revert(ex)
            InstantCommit(stopped)
        }
    }
    tx(f2){ e => timers => {
      timers.cancelAll()
      revert(e)
      }
    }
  }


  def compensate[A <: Out](f: Log => A => Commit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    val f2: TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A] = _ => _ => f
    tx(f2){ e => timers => {
        timers.cancelAll()
        throwConsistencyException
      }
    }
  }

  def compensate[A <: Out](retry: Throwable => Duration)(f: Log => A => InstantCommit[A])(implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
    val f2: TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A] = timers => _ => log => a => Try {
      f(log)(a)
    } match {
      case Success(v) => v
      case Failure(e) =>
        log.error(e.getMessage, e)
        Try {
          timers.startSingleTimer(Payload(a), FiniteDuration(retry(e).toMillis, MILLISECONDS))
        } match {
          case Success(_) =>
            InstantCommit(same)
          case Failure(ex) =>
            throwConsistencyException
            InstantCommit(stopped)
        }

    }
    if (sc.executionExpired.isBefore(LocalDateTime.now())) throwConsistencyException
    tx(f2){ _ => timers => {
        timers.cancelAll()
        throwConsistencyException
      }
    }
  }

  private def throwConsistencyException[A](implicit sc: SagaContext[A]): Behavior[Message[A]] = {
    sc.executor ! SagaConsistencyException(sc.id)
    throw new ConsistencyException(sc.id)
  }
}