package cinema.retry.suspend.transaction

import java.time.LocalDateTime

import akka.actor.typed.ActorRef
import cinema.RetryStrategy
import cinema.exception.{ExpectedTestException, OtherExpectedTestException}
import cinema.message.Message
import cinema.retry.suspend.actor.Calc
import cinema.retry.suspend.actor.Calc.Calc
import cinema.retry.suspend.actor.Calc.Inc
import cinema.retry.suspend.transaction.OutMessage.{OutMessage, SomeOutMessage}
import cinema.retry.suspend.transaction.RetryMessage.{CalcMessage, InMessage, RetryMessage, SecondInMessage}
import cinema.saga.context.SagaContext
import cinema.transaction.suspend.SuspendTransaction

import scala.concurrent.duration._
import scala.language.postfixOps

object RetrySuspendTransaction extends SuspendTransaction[RetryMessage, OutMessage] {

  val retryStrategy: RetryStrategy = {
    case e: ExpectedTestException => 2 seconds
    case _ => throw new OtherExpectedTestException
  }

  override def apply(implicit sc: SagaContext[RetryMessage]): Apply = execute(retryStrategy) { self => _ => {
      case CalcMessage(i) =>
        commit(SomeOutMessage(i))
      case InMessage() =>
        if (LocalDateTime.now().isAfter(sc.executionExpired.minusSeconds(25))) {
          actorSelection[Calc]{ actorRef =>
            actorRef ! Inc(42, self.asInstanceOf[ActorRef[Message[Any]]])
          }
          await
        }
        else {
          throw new ExpectedTestException
        }
      case SecondInMessage() =>
        throw new RuntimeException
    }
  }

  override def unapply(implicit sc: SagaContext[OutMessage]): UnApply = skip
}