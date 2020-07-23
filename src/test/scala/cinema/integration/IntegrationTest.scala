package cinema.integration

import akka.actor.typed.DispatcherSelector._
import cinema.app.CinemaAware
import cinema.exception.UnexpectedTestException
import cinema.integration.first.FirstTransaction
import cinema.integration.messages.ZeroFirst.First
import cinema.integration.second.SecondTransaction
import cinema.integration.third.ThirdTransaction
import cinema.saga.builder.SagaBuilder
import cinema.transaction.exception.TransactionException
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class IntegrationTest extends FlatSpec with CinemaAware {

  "Success Application" should "complete saga" in {
    val future = SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction, blocking)
      .transaction(ThirdTransaction)
      .build()
      .duration(100 seconds)
      .run(First(1))

    val result = Await.result(future, 100 seconds)
    assert(result)
  }

  "Failed Application" should "rollback transaction" in {
    val future = SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction, blocking)
      .transaction(ThirdTransaction)
      .build()
      .duration(1000 seconds, compensateDuration = 30 seconds)
      .run(First(-1))

    future onComplete {
      case Success(v) => throw new UnexpectedTestException
      case Failure(e) => assert(e != null)
    }

    assertThrows[TransactionException] {
      Await.result(future, 100 seconds)
    }
  }


  "Timeout Application" should "rollback transaction" in {
    val future = SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction, blocking)
      .transaction(ThirdTransaction)
      .build()
      .duration(duration = 3 seconds, compensateDuration = 15 seconds)
      .run(First(0))

    future onComplete {
      case Success(v) => throw new UnexpectedTestException
      case Failure(e) => assert(e != null)
    }

    assertThrows[TransactionException] {
      Await.result(future, 100 seconds)
    }
  }
}
