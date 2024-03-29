package cinema.stateless

import akka.actor.typed.DispatcherSelector.blocking
import cinema.app.CinemaAware
import cinema.saga.builder.SagaBuilder
import cinema.stateless.transaction.SomeStatelessTransaction
import cinema.stateless.transaction.StatelessMessages.{InMessage, OutMessage}
import org.scalatest.Matchers.convertToAnyShouldWrapper
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class StatelessTest extends FlatSpec with CinemaAware {

  "Retry" should "complete" in {
    val future = SagaBuilder()
      .transaction(SomeStatelessTransaction, blocking)
      .build()
      .duration(100 seconds)
      .run(InMessage())

    val result = Await.result(future, 100 seconds)
    result shouldBe OutMessage()
  }
}