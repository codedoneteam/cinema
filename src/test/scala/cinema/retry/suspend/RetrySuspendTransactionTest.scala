package cinema.retry.suspend

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.exception.OtherExpectedTestException
import cinema.retry.suspend.actor.Calc
import cinema.retry.suspend.transaction.OutMessage.{OutMessage, SomeOutMessage}
import cinema.retry.suspend.transaction.RetryMessage.{InMessage, SecondInMessage}
import cinema.retry.suspend.transaction.RetrySuspendTransaction
import cinema.saga.builder.SagaBuilder
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps

class RetrySuspendTransactionTest extends FlatSpec with CinemaAware {

  "Suspend Retry" should "complete" in {
    val promise = Promise[OutMessage]()
    actorOf(Calc()) { _ =>
      val future = SagaBuilder()
        .transaction(RetrySuspendTransaction, blocking)
        .build()
        .duration(30 seconds)
        .run(InMessage())
      promise.completeWith(future)
    }
    val result = Await.result(promise.future, 30 seconds)
    assert(result == SomeOutMessage(43))
  }

  "Some retry" should "throw exception" in {
    val promise = Promise[OutMessage]()
    actorOf(Calc()) { _ =>
      val future = SagaBuilder()
        .transaction(RetrySuspendTransaction, blocking)
        .build()
        .duration(10 seconds)
        .run(SecondInMessage())
      promise.completeWith(future)
    }

    assertThrows[OtherExpectedTestException] {
      Await.result(promise.future, 100 seconds)
    }
  }
}