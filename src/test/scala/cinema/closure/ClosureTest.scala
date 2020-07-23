package cinema.closure

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.closure.ClosureMessages.{InMessage, OutMessage}
import cinema.saga.builder.SagaBuilder
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class ClosureTest extends FlatSpec with CinemaAware {

  "Closure" should "complete" in {
    val immutableSharedState = 42
    val future = SagaBuilder(immutableSharedState)
      .transaction(SomeClosureTransaction, blocking)
      .build()
      .duration(100 seconds)
      .run(InMessage())

    val result = Await.result(future, 100 seconds)
    assert(result == OutMessage(42))
  }
}