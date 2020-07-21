package cinema.rollback

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.DispatcherSelector
import cinema.exception.{ExpectedTestException, UnexpectedTestException}
import cinema.hvector.HVector.HNil
import cinema.message.Payload
import cinema.rollback.transaction.RollbackTransaction
import cinema.rollback.transaction.TestActorMessage.Send
import cinema.saga.context.SagaContext
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class TransactionRollbackTest extends WordSpecLike with BeforeAndAfterAll with Matchers {

  "Transaction" must  {
     "rollback" in {
       val testKit = ActorTestKit()
       val promise = Promise[Boolean]()
       val actor = RollbackTransaction(SagaContext(in = Send,
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
       ref ! Payload(Send)
       promise.future onComplete {
         case Success(v) => throw new UnexpectedTestException
         case Failure(e) => assert(e != null)
       }

       assertThrows[ExpectedTestException] {
         Await.result(promise.future, 10 seconds)
       }
     }

  }
}