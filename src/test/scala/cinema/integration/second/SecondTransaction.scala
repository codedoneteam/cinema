package cinema.integration.second

import java.time.LocalDateTime

import cinema.exception.{ExpectedTestException, UnexpectedTestException}
import cinema.integration.messages.FirstMessage.{FirstSecondMessage, Second}
import cinema.integration.messages.SecondThird.{SecondThirdMessage, Third}
import cinema.log.Log
import cinema.saga.context.SagaContext
import cinema.transaction.stateless.Transaction

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object SecondTransaction extends Transaction[FirstSecondMessage, SecondThirdMessage] {

  val retryStrategy: Throwable => Duration = _ => 2 seconds

  override def apply(implicit sc: SagaContext[FirstSecondMessage]): Apply = execute { log: Log => {
    case Second(i) =>
      implicit val ec: ExecutionContext = executionContext
      commitFuture {
        Future {
          Thread sleep 2000
          Third(i)
        }
      }
    case x => throw new ExpectedTestException
    }
  }

  override def unapply(implicit sc: SagaContext[SecondThirdMessage]): UnApply = compensate(retryStrategy) { _ => {
      case msg: Third if msg.i == -1 =>
        implicit val ec: ExecutionContext = executionContext
        val failUntil = sc.executionExpired.minusSeconds(5)
        val now = LocalDateTime.now()
       if (now.isBefore(failUntil)) throw new ExpectedTestException
        commit(Second(msg.i))
      case _ =>
        throw new UnexpectedTestException
    }
  }
}