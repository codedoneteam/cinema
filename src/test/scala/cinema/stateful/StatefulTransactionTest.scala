package cinema.stateful

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.DispatcherSelector
import cinema.exception.UnexpectedTestException
import cinema.hvector.HVector.HNil
import cinema.message.Payload
import cinema.saga.context.SagaContext
import cinema.stateful.TestMessage.First
import cinema.stateful.TestOutMessage.{TestOut, TestOutMessage}
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class StatefulTransactionTest extends WordSpecLike with BeforeAndAfterAll with Matchers {

  "Transaction" must  {
     "success" in {
       val testKit = ActorTestKit()
       val promise = Promise[TestOutMessage]()
       val actor = SomeStatefulTransaction(Some(SomeState()))(SagaContext(in = First,
         system = null,
         dispatcherSelector = DispatcherSelector.defaultDispatcher(),
         id = UUID.randomUUID(),
         executionExpired = LocalDateTime.now().plusSeconds(100),
         duration = 100 seconds,
         compensateDuration = 100 seconds,
         executor = null,
         config = null,
         forward = HNil,
         reverse = HNil,
         promise = promise,
         messages = HNil,
         closure = HNil))
       val ref = testKit.spawn(actor, "test")
       ref ! Payload(First)

       promise.future onComplete {
         case Success(v) => assert(v == TestOut)
         case Failure(e) => throw new UnexpectedTestException
       }

       Await.result(promise.future, 10 seconds)
     }

  }
}