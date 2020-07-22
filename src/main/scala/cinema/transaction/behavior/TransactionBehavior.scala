package cinema.transaction.behavior

import java.time.LocalDateTime

import akka.actor.typed.scaladsl.Behaviors._
import akka.actor.typed.scaladsl.TimerScheduler
import akka.actor.typed.{ActorRef, Behavior}
import cinema.log.Log
import cinema.message.{Fail, Message, Payload, Timeout}
import cinema.saga.context.SagaContext
import cinema.transaction.commit.Commit
import cinema.transaction.exception.TransactionTimeoutException

import scala.concurrent.duration.{FiniteDuration, _}
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Failure, Success, Try}

trait TransactionBehavior[In, Out] extends ExecutionBehavior with ApplyBehavior[In, Out] with UnApplyBehavior[In, Out] {

    protected def tx[A](f: TimerScheduler[Message[A]] => ActorRef[Message[A]] => Log => A => Commit[A])
                       (exceptionHandler: Throwable => TimerScheduler[Message[A]] => Behavior[Message[A]])
                       (implicit sc: SagaContext[A], typeTag: TypeTag[A]): Behavior[Message[A]] = {
      withTimers[Message[A]] { timers =>
        withMdc[Message[A]](Map("saga" -> sc.id.toString)) {
          receive[Message[A]]((ctx, message) =>
            Try {
              message match {
                case Payload(data) =>
                  timers.startSingleTimer(Timeout, timeout(sc))
                  f(timers)(ctx.self)(Log(ctx))(data)
                case Timeout => throw new TransactionTimeoutException
                case Fail(e) => throw e
              }
             } match {
                case Success(out) => out.behavior
                case Failure(e) => exceptionHandler(e)(timers)
            }
          )
        }
      }
  }

  private def timeout[A](sc: SagaContext[A]): FiniteDuration = {
    import java.time.temporal.ChronoUnit
    val diff = ChronoUnit.NANOS.between(LocalDateTime.now(), sc.executionExpired)
    FiniteDuration(diff, NANOSECONDS)
  }
}