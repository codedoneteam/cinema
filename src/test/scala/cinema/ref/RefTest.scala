package cinema.ref

import cinema.app.CinemaAware
import cinema.ref.ReplayActor.Ping
import org.scalatest.FlatSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps

class RefTest extends FlatSpec with CinemaAware {

  "Produce actor" should "run" in {

    val promise = Promise[Boolean]()

    actorOf(ReplayActor()) { ref =>
      ref ! Ping(promise)
    }

    val result = Await.result(promise.future, 100 seconds)
    assert(result)
  }

}