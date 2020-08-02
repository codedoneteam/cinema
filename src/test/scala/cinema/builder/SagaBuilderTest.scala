package cinema.builder

import cinema.app.CinemaAware
import cinema.builder.transaction.{FirstTransaction, SecondTransaction, ThirdTransaction}
import cinema.exception.SagaBuilderException
import cinema.saga.builder.SagaBuilder
import org.scalatest.FlatSpec

import scala.language.postfixOps

class SagaBuilderTest extends FlatSpec with CinemaAware {

  "Saga builder" should "build saga" in {
    assert(SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction)
      .build() != null)
  }

  "Saga builder" should "failure" in {
    assertThrows[SagaBuilderException[_]] {
      SagaBuilder()
        .transaction(FirstTransaction)
        .transaction(SecondTransaction)
        .transaction(ThirdTransaction)
        .build()
    }
  }
}