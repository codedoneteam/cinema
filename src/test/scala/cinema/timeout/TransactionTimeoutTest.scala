package cinema.timeout

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.DispatcherSelector
import cinema.exception.UnexpectedTestException
import cinema.hvector.HVector.HNil
import cinema.message.Payload
import cinema.saga.context.SagaContext
import cinema.timeout.transaction.message.TestMessage.Send
import cinema.timeout.transaction.state.SomeState
import cinema.timeout.transaction.{CheckedTimeoutTransaction, StatefulTimeoutTransaction}
import cinema.transaction.exception.TransactionTimeoutException
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class TransactionTimeoutTest extends WordSpecLike with BeforeAndAfterAll with Matchers {

  "Timeout transaction" must  {
     "fail" in {
       val testKit = ActorTestKit()
       val promise = Promise[Boolean]()
       val behavior = CheckedTimeoutTransaction(Some(SomeState(0)))(SagaContext(in = Send,
                                        system = null,
                                        dispatcherSelector = DispatcherSelector.defaultDispatcher(),
                                        id = UUID.randomUUID(),
                                        executionExpired = LocalDateTime.now().minusSeconds(100),
                                        duration = 100 seconds,
                                        compensateDuration = 100 seconds,
                                        executor = null,
                                        config = null,
                                        forward = HNil,
                                        reverse = HNil,
                                        promise = promise,
                                        messages = HNil,
                                        closure = HNil))
       val ref = testKit.spawn(behavior, "test")
       ref ! Payload(Send)
       promise.future onComplete {
         case Success(v) => throw new UnexpectedTestException
         case Failure(e) => assert(e != null)
       }

       assertThrows[TransactionTimeoutException] {
         Await.result(promise.future, 10 seconds)
       }
     }
  }

  "Stateful transaction timeout" must  {
    "fail" in {
      val testKit = ActorTestKit()
      val promise = Promise[Boolean]()
      val actor = StatefulTimeoutTransaction(None)(SagaContext(in = Send,
        system = null,
        dispatcherSelector = DispatcherSelector.defaultDispatcher(),
        id = UUID.randomUUID(),
        executionExpired = LocalDateTime.now().plusSeconds(5),
        duration = 5 seconds,
        compensateDuration = 5 seconds,
        executor = null,
        config = null,
        forward = HNil,
        reverse = HNil,
        promise = promise,
        messages = HNil,
        closure = HNil))
      val ref = testKit.spawn(actor, "test")
      ref ! Payload(Send)
      promise.future onComplete {
        case Success(v) => throw new UnexpectedTestException
        case Failure(e) => assert(e != null)
      }

      assertThrows[TransactionTimeoutException] {
        Await.result(promise.future, 10 seconds)
      }
    }
  }
}