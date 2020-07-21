package cinema.retry.suspend

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.exception.OtherExpectedTestException
import cinema.retry.suspend.OutMessage.SomeOutMessage
import cinema.retry.suspend.RetryMessage.{InMessage, SecondInMessage}
import cinema.saga.builder.SagaBuilder
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class RetrySuspendTransactionTest extends FlatSpec with CinemaAware {

  "Suspend Retry" should "complete" in {
    val future = SagaBuilder()
      .transaction(RetrySuspendTransaction, blocking)
      .duration(30 seconds)
      .run(InMessage())

    val result = Await.result(future, 30 seconds)
    assert(result == SomeOutMessage(43))
  }

  "Some retry" should "throw exception" in {
    val future = SagaBuilder()
      .transaction(RetrySuspendTransaction, blocking)
      .duration(10 seconds)
      .run(SecondInMessage())

    assertThrows[OtherExpectedTestException] {
      Await.result(future, 100 seconds)
    }
  }
}