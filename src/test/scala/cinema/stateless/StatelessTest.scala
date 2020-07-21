package cinema.stateless

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.saga.builder.SagaBuilder
import cinema.stateless.StatelessMessages.{InMessage, OutMessage}
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class StatelessTest extends FlatSpec with CinemaAware {

  "Retry" should "complete" in {
    val future = SagaBuilder()
      .transaction(SomeStatelessTransaction, blocking)
      .duration(100 seconds)
      .run(InMessage())

    val result = Await.result(future, 100 seconds)
    assert(result == OutMessage())
  }
}