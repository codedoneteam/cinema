package cinema.scheduler

import cinema.app.CinemaAware
import org.scalatest.FlatSpec
import org.scalatest.Matchers.convertToAnyShouldWrapper

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.Try

class SchedulerAwareTest extends FlatSpec with CinemaAware {

  "After" should "run action" in {
      val promise = Promise[Int]()
      after(1 second){
        promise.complete(Try(42))
      }
     Await.result(promise.future, 5 seconds) shouldBe 42
  }

  "Every 1 seconds" should "run action" in {
    val promise = Promise[Int]()
    every(1 second){
      if (!promise.isCompleted) promise.complete(Try(43))
    }
    Await.result(promise.future, 5 seconds) shouldBe 43
  }
}