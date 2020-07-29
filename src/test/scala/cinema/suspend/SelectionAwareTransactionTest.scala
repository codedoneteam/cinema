package cinema.suspend

import cinema.app.CinemaAware
import cinema.saga.builder.SagaBuilder
import cinema.suspend.transaction.InMessage.One
import cinema.suspend.transaction.OutMessage.Two
import cinema.suspend.actor.Calc
import cinema.suspend.transaction.SelectionAwareTransaction
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps

class SelectionAwareTransactionTest extends FlatSpec with CinemaAware {

  "Selection aware actor" should "interact with actor" in {
    val promise = Promise[Two]()
    actorOf(Calc()) { ref =>
      val future = SagaBuilder(ref)
        .transaction(SelectionAwareTransaction)
        .build()
        .duration(100 seconds)
        .run(One(42))
      promise.completeWith(future)
    }
    val result = Await.result(promise.future, 100 seconds)
    assert(result == Two(43))
  }

}