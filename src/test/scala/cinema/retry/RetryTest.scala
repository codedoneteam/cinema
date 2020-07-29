package cinema.retry

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.exception.OtherExpectedTestException
import cinema.retry.transaction.OutMessage.SomeOutMessage
import cinema.retry.transaction.RetryMessage.{InMessage, SecondInMessage}
import cinema.retry.transaction.SomeRetryTransaction
import cinema.saga.builder.SagaBuilder
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class RetryTest extends FlatSpec with CinemaAware {

  "Retry" should "complete" in {
    val future = SagaBuilder()
      .transaction(SomeRetryTransaction, blocking)
      .build()
      .duration(10 seconds)
      .run(InMessage())

    val result = Await.result(future, 10 seconds)
    assert(result == SomeOutMessage())
  }

  "Some retry" should "throw exception" in {
    val future = SagaBuilder()
      .transaction(SomeRetryTransaction, blocking)
      .build()
      .duration(10 seconds)
      .run(SecondInMessage())

    assertThrows[OtherExpectedTestException] {
      Await.result(future, 100 seconds)
    }
  }
}