package cinema.saga

import cinema.app.CinemaAware
import cinema.exception.UnexpectedTestException
import cinema.saga.FailureMessages.InMessage
import cinema.saga.builder.SagaBuilder
import cinema.transaction.exception.SagaException
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class SagaFailureTest extends FlatSpec with CinemaAware {

  "Saga" should "failure" in {
    val future = SagaBuilder()
      .transaction(SomeFailureTransaction)
      .build()
      .duration(100 seconds)
      .run(InMessage(42))

    future.onComplete {
      case Success(v) => throw new UnexpectedTestException
      case Failure(e: SagaException[_]) => assert(e.message == InMessage(42))
      case Failure(_) => throw new UnexpectedTestException
    }

    assertThrows[SagaException[_]] {
      Await.result(future, 100 seconds)
    }
  }
}