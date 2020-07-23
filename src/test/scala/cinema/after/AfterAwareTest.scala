package cinema.after

import cinema.app.CinemaAware
import org.scalatest.FlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.Try

class AfterAwareTest extends FlatSpec with CinemaAware {

  "After" should "run action" in {
      val promise = Promise[Int]()
      after(1 second){
        promise.complete(Try(42))
      }
     assert(Await.result(promise.future, 5 seconds) == 42)
  }

}
