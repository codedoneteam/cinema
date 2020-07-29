package cinema.config

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.typed.DispatcherSelector
import cinema.config.Config.$
import cinema.hvector.HVector.HNil
import cinema.saga.context.SagaContext
import cinema.stateful.TestMessage
import com.typesafe.config.ConfigFactory
import org.scalatest.WordSpecLike

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps

class NormalizeTest extends WordSpecLike with NormalizeAware {

  "Normalize" must {
    "normalize path" in {
      assert(normalizePath("d") == "d")
      assert(normalizePath("TestOne") == "test-one")
    }

    "normalize key" in {
      assert(normalizeKey("d") == "d")
      assert(normalizeKey("test-one") == "testOne")
    }
  }
}