package cinema.retry

import java.time.LocalDateTime

import cinema.RetryStrategy
import cinema.exception.{ExpectedTestException, OtherExpectedTestException}
import cinema.retry.OutMessage.{OutMessage, SomeOutMessage}
import cinema.retry.RetryMessage.{InMessage, RetryMessage, SecondInMessage}
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.concurrent.duration._
import scala.language.postfixOps

object SomeRetryTransaction extends Transaction[RetryMessage, OutMessage] {

  val retryStrategy: RetryStrategy = {
    case e: ExpectedTestException => 2 seconds
    case _ => throw new OtherExpectedTestException
  }

  override def apply(implicit sc: SagaContext[RetryMessage]): Apply = execute(retryStrategy) { _ => {
      case InMessage() =>
        if (LocalDateTime.now().isAfter(sc.executionExpired.minusSeconds(5))) {
          commit(SomeOutMessage())
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