package cinema.config

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.typed.DispatcherSelector
import cinema.hvector.HVector.HNil
import cinema.saga.context.SagaContext
import cinema.stateful.TestMessage
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps

class ConfigTest extends WordSpecLike {

  implicit val sc: SagaContext[TestMessage.First.type] = SagaContext(in = null,
    system = null,
    dispatcherSelector = DispatcherSelector.defaultDispatcher(),
    id = UUID.randomUUID(),
    executionExpired = LocalDateTime.now().plusSeconds(100),
    duration = 100 seconds,
    compensateDuration = 100 seconds,
    executor = null,
    config = ConfigFactory.load(),
    forward = HNil,
    reverse = HNil,
    promise = Promise(),
    messages = HNil,
    closure = HNil)

  "Config" must {
    "create instance" in {
      val firstConfig = $[First]()
      assert(firstConfig.s == "TEST")
      assert(firstConfig.n == 42)
      assert(firstConfig.d == 1.42)
    }

    "create instance with defaults" in {
      val firstConfig = $[Second]()
      assert(firstConfig.s == "DATA")
    }

    "create instance from path" in {
      val firstConfig = $[Third]("third.test")
      assert(firstConfig.s == "TEST")
    }
  }
}