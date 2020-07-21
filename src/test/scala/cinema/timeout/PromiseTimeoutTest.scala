package cinema.timeout

import cinema.app.CinemaAware
import cinema.saga.builder.SagaBuilder
import cinema.timeout.transaction.message.PromiseMessage.InPromiseMessage
import cinema.timeout.transaction.{PromiseStatefulTransaction, PromiseTransaction}
import cinema.transaction.exception.{SagaException, TransactionTimeoutException}
import org.scalatest.FlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps

class PromiseTimeoutTest extends FlatSpec with CinemaAware {

  "Check" should "timeout" in {
    val promise = Promise[Boolean]()

    val future = SagaBuilder()
      .transaction(PromiseTransaction)
      .duration(1 seconds)
      .run(InPromiseMessage(-1, promise))

    assertThrows[TransactionTimeoutException] {
      Await.result(future, 5 seconds)
      assert(promise.isCompleted)
    }
  }

  "Commit Transaction" should "timeout" in {
    val promise = Promise[Boolean]()

    val future = SagaBuilder()
      .transaction(PromiseTransaction)
      .duration(1 seconds)
      .run(InPromiseMessage(0, promise))

    assertThrows[SagaException[_]] {
      Await.result(future, 5 seconds)
      assert(promise.isCompleted)
    }
  }

  "Transaction" should "throw timeout exception" in {
    val promise = Promise[Boolean]()

    val future = SagaBuilder()
      .transaction(PromiseTransaction)
      .duration(1 seconds)
      .run(InPromiseMessage(1, promise))

    assertThrows[SagaException[_]] {
      Await.result(future, 10 seconds)
      assert(promise.isCompleted)
    }
  }

  "Stateful transaction" should "timeout" in {
    val promise = Promise[Boolean]()

    val future = SagaBuilder()
      .transaction(PromiseStatefulTransaction)
      .duration(1 seconds)
      .run(InPromiseMessage(2, promise))

    assertThrows[SagaException[_]] {
      Await.result(future, 10 seconds)
      assert(promise.isCompleted)
    }
  }
}