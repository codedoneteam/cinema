package cinema.future

import cinema.app.CinemaAware
import cinema.saga.builder.SagaBuilder
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class FutureTest extends FlatSpec with CinemaAware {

  "Future Transaction" should "commit" in {
    val future = SagaBuilder()
      .transaction(FutureTransaction)
      .duration(100 seconds)
      .run(1)

    val result = Await.result(future, 100 seconds)
    assert(result == "1")
  }

}