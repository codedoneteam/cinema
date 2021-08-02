package cinema.builder

import cinema.app.CinemaAware
import cinema.builder.transaction.{FirstTransaction, SecondTransaction, ThirdTransaction}
import cinema.exception.SagaBuilderException
import cinema.saga.builder.SagaBuilder
import org.scalatest.FlatSpec
import org.scalatest.Matchers.{convertToAnyShouldWrapper, not}
import scala.language.postfixOps

class SagaBuilderTest extends FlatSpec with CinemaAware {

  "Saga builder" should "build saga" in {
    SagaBuilder()
      .transaction(FirstTransaction)
      .transaction(SecondTransaction)
      .build() should not be null
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