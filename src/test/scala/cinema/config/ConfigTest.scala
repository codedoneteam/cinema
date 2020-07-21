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

  "Config" must {
    "create instance" in {
      val config = ConfigFactory.load()
      implicit val sc: SagaContext[TestMessage.First.type] = SagaContext(in = null,
                            system = null,
                            dispatcherSelector = DispatcherSelector.defaultDispatcher(),
                            id = UUID.randomUUID(),
                            executionExpired = LocalDateTime.now().plusSeconds(100),
                            duration = 100 seconds,
                            compensateDuration = 100 seconds,
                            executor = null,
                            config = config,
                            forward = HNil,
                            reverse = HNil,
                            promise = Promise(),
                            messages = HNil,
                            closure = HNil)

      val firstConfig = Config[First]("first.test")
      assert(firstConfig.s == "TEST")
      assert(firstConfig.n == 42)
      assert(firstConfig.d == 1.42)
    }
  }
}