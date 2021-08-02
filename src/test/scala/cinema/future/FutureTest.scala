package cinema.future

import cinema.app.CinemaAware
import cinema.future.transaction.FutureTransaction
import cinema.saga.builder.SagaBuilder
import org.scalatest.FlatSpec
import org.scalatest.Matchers.convertToAnyShouldWrapper

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class FutureTest extends FlatSpec with CinemaAware {

  "Future Transaction" should "commit" in {
    val future = SagaBuilder()
      .transaction(FutureTransaction)
      .build()
      .duration(100 seconds)
      .run(1)

    val result = Await.result(future, 100 seconds)
    result shouldBe "1"
  }

}